package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.InvalidUrlException;
import com.fastify.musicdownload.exception.UnableToDownloadException;
import com.fastify.musicdownload.model.DownloadResult;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class YoutubeDownloadService implements MusicDownloadService {

    private final String outputPath;
    private final String audioFormat;
    private final String thumbnailFormat;
    private final String scriptName;
    private final String scriptResourcePath;
    private final S3Service s3Service;
    private final DownloadTimeoutService downloadTimeoutService;

    public YoutubeDownloadService(
            @Value("${download.out.path}") String outputPath,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.thumbnail}") String thumbnailFormat,
            @Value("${download.script.run}") String scriptName,
            @Value("${download.script.resource.path}") String scriptResourcePath,
            S3Service s3Service,
            DownloadTimeoutService downloadTimeoutService
    ) {
        this.outputPath = outputPath;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.scriptName = scriptName;
        this.scriptResourcePath = scriptResourcePath;
        this.s3Service = s3Service;
        this.downloadTimeoutService = downloadTimeoutService;
    }

    @Override
    public void download(MusicDownloadDto musicDownloadDto) {
        String youtubeUrl = musicDownloadDto.url();
        validateYoutubeUrl(youtubeUrl);

        String scriptsDirPath = Objects.requireNonNull(getClass().getResource(scriptResourcePath)).getPath();

        ProcessBuilder processBuilder = new ProcessBuilder(
                scriptName,
                youtubeUrl,
                outputPath,
                audioFormat,
                thumbnailFormat
        );
        processBuilder.directory(Paths.get(scriptsDirPath).toFile());

        try {
            Process process = processBuilder.start();

            downloadTimeoutService.startTimeout(process);

            DownloadResult downloadResult = getDownloadResult(process);
            System.out.println("downloadResult: " + downloadResult);
        } catch (IOException | JsonParseException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        }
    }

    private DownloadResult getDownloadResult(Process process) throws IOException, JsonParseException {
        String downloadResultJson = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8)
                .lines()
                .filter(this::relatedToJson)
                .collect(Collectors.joining("\n"));

        return new Gson().fromJson(downloadResultJson, DownloadResult.class);
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
