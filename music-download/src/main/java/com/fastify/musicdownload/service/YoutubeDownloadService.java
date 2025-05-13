package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.InvalidUrlException;
import com.fastify.musicdownload.exception.UnableToDownloadException;
import com.fastify.musicdownload.model.DownloadResult;
import com.fastify.musicdownload.model.PreDownloadValidationResult;
import com.fastify.musicdownload.model.dto.DownloadResultDto;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.fastify.musicdownload.util.CloseableProcess;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class YoutubeDownloadService implements MusicDownloadService {

    private final String outputPath;
    private final String audioFormat;
    private final String thumbnailFormat;
    private final String youtubeDownloadScriptName;
    private final String youtubeValidateScriptName;
    private final String scriptResourcePath;
    private final S3Service s3Service;

    public YoutubeDownloadService(
            @Value("${download.out.path}") String outputPath,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.thumbnail}") String thumbnailFormat,
            @Value("${download.scripts.download}") String youtubeDownloadScriptName,
            @Value("${download.scripts.validate}") String youtubeValidateScriptName,
            @Value("${download.scripts.resource-path}") String scriptResourcePath,
            S3Service s3Service
    ) {
        this.outputPath = outputPath;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.youtubeDownloadScriptName = youtubeDownloadScriptName;
        this.youtubeValidateScriptName = youtubeValidateScriptName;
        this.scriptResourcePath = scriptResourcePath;
        this.s3Service = s3Service;
    }

    @Override
    public DownloadResultDto download(MusicDownloadDto musicDownloadDto) {
        String youtubeUrl = musicDownloadDto.url();
        validateYoutubeUrl(youtubeUrl);

        ProcessBuilder validateDownloadScriptName = buildScriptProcess(
                youtubeValidateScriptName,
                youtubeUrl
        );

        PreDownloadValidationResult preDownloadValidationResult;
        try (CloseableProcess closeableProcess = new CloseableProcess(validateDownloadScriptName.start());
             InputStream inputStream = closeableProcess.getInputStream();
             InputStream errorStream = closeableProcess.getErrorStream()
        ) {
            preDownloadValidationResult = readInputStream(inputStream, PreDownloadValidationResult.class);
            if (errorStream.readAllBytes().length != 0) {
                throw new UnableToDownloadException("Unexpected error during url validation");
            }
        } catch (IOException e) {
            throw new UnableToDownloadException("Unexpected error during url validation");
        }

        if (preDownloadValidationResult.filesizeMb() > 100) {
            throw new RuntimeException();
        }

        ProcessBuilder processBuilder = buildScriptProcess(
                youtubeDownloadScriptName,
                youtubeUrl,
                outputPath,
                audioFormat,
                thumbnailFormat
        );

        DownloadResult downloadResult;
        try (CloseableProcess closeableProcess = new CloseableProcess(processBuilder.start());
             InputStream inputStream = closeableProcess.getInputStream()) {
            downloadResult = readInputStream(inputStream, DownloadResult.class);
        } catch (IOException | JsonParseException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        }

        return DownloadResultDto.builder()
                .videoId(downloadResult.videoId())
                .uploader(downloadResult.uploader())
                .title(downloadResult.title())
                .build();
    }

    private <T> T readInputStream(InputStream inputStream, Class<T> clazz) throws IOException, JsonParseException {
        String downloadResultJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
                .lines()
                .filter(this::relatedToJson)
                .collect(Collectors.joining("\n"));

        return new Gson().fromJson(downloadResultJson, clazz);
    }

    private ProcessBuilder buildScriptProcess(String... args) {
        String scriptsDirPath = Objects.requireNonNull(getClass().getResource(scriptResourcePath)).getPath();
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.directory(Paths.get(scriptsDirPath).toFile());

        return processBuilder;
    }

    private void validateYoutubeUrl(String url) {
        String regex = "^http[s,]://.*youtube.*";
        if (!url.matches(regex)) {
            throw new InvalidUrlException(url + " is not a valid Youtube url");
        }
    }

    private boolean relatedToJson(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("{") || trimmed.startsWith("}") || trimmed.startsWith("\"");
    }
}
