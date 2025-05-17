package com.fastify.download.service;

import com.fastify.download.controller.kafka.producer.MusicCommandProducer;
import com.fastify.download.exception.DownloadTooLargeException;
import com.fastify.download.exception.InvalidUrlException;
import com.fastify.download.exception.UnableToDownloadException;
import com.fastify.download.model.DownloadResult;
import com.fastify.download.model.PreDownloadValidationResult;
import com.fastify.download.model.dto.DownloadResultDto;
import com.fastify.download.model.dto.MusicDownloadDto;
import com.fastify.download.model.entity.Music;
import com.fastify.download.model.entity.User;
import com.fastify.download.model.event.MusicDownloadedEvent;
import com.fastify.download.util.CloseableProcess;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final Long downloadSizeThresholdMb;
    private final UserService userService;
    private final S3Service s3Service;
    private final MusicCommandProducer musicCommandProducer;

    public YoutubeDownloadService(
            @Value("${download.out.path}") String outputPath,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.thumbnail}") String thumbnailFormat,
            @Value("${download.scripts.download}") String youtubeDownloadScriptName,
            @Value("${download.scripts.validate}") String youtubeValidateScriptName,
            @Value("${download.scripts.resource-path}") String scriptResourcePath,
            @Value("${download.size.threshold.mb}") Long downloadSizeThresholdMb,
            UserService userService,
            S3Service s3Service, MusicCommandProducer musicCommandProducer
    ) {
        this.outputPath = outputPath;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.youtubeDownloadScriptName = youtubeDownloadScriptName;
        this.youtubeValidateScriptName = youtubeValidateScriptName;
        this.scriptResourcePath = scriptResourcePath;
        this.downloadSizeThresholdMb = downloadSizeThresholdMb;
        this.userService = userService;
        this.s3Service = s3Service;
        this.musicCommandProducer = musicCommandProducer;
    }

    @Override
    @Transactional
    public DownloadResultDto download(User user, MusicDownloadDto musicDownloadDto) {
        String youtubeUrl = musicDownloadDto.url();
        validateYoutubeUrl(youtubeUrl);
        Music music = userService.addDownloadedMusic(user, youtubeUrl);

        ProcessBuilder validateDownloadScriptName = buildScriptProcess(
                youtubeValidateScriptName,
                youtubeUrl
        );

        PreDownloadValidationResult preDownloadValidationResult = readProcessInputStream(validateDownloadScriptName, PreDownloadValidationResult.class);

        if (preDownloadValidationResult.filesizeMb() > downloadSizeThresholdMb) {
            throw new DownloadTooLargeException("Files larger then " + downloadSizeThresholdMb + "MB are not supported");
        }

        ProcessBuilder processBuilder = buildScriptProcess(
                youtubeDownloadScriptName,
                youtubeUrl,
                outputPath,
                audioFormat,
                thumbnailFormat
        );

        DownloadResult downloadResult = readProcessInputStream(processBuilder, DownloadResult.class);
        s3Service.store(user.getId(), downloadResult);

        MusicDownloadedEvent musicDownloadedEvent = new MusicDownloadedEvent(user.getId(), music.getId(), downloadResult.videoId(), music.getUrl());
        musicCommandProducer.sendMusicDownloadedEvent(musicDownloadedEvent);

        return DownloadResultDto.builder()
                .videoId(downloadResult.videoId())
                .url(youtubeUrl)
                .uploader(downloadResult.uploader())
                .title(downloadResult.title())
                .build();
    }

    private <T> T readProcessInputStream(ProcessBuilder processBuilder, Class<T> clazz) {
        try (CloseableProcess closeableProcess = new CloseableProcess(processBuilder.start());
             InputStream inputStream = closeableProcess.getInputStream();
             InputStream errorStream = closeableProcess.getErrorStream()) {
            T t = readInputStream(inputStream, clazz);
            if (errorStream.readAllBytes().length != 0) {
                throw new UnableToDownloadException("Unexpected error during url validation");
            }
            return t;
        } catch (IOException | JsonParseException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        }
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
