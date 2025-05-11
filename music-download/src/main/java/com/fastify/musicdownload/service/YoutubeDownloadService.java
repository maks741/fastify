package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.DownloadTimeoutException;
import com.fastify.musicdownload.exception.InvalidUrlException;
import com.fastify.musicdownload.exception.UnableToDownloadException;
import com.fastify.musicdownload.model.DownloadResult;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class YoutubeDownloadService implements MusicDownloadService {

    private final String outputPath;
    private final String audioFormat;
    private final String thumbnailFormat;
    private final String scriptName;
    private final String scriptResourcePath;
    private final Long downloadTimeoutMillis;
    private final S3Service s3Service;

    public YoutubeDownloadService(
            @Value("${download.out.path}") String outputPath,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.thumbnail}") String thumbnailFormat,
            @Value("${download.script.run}") String scriptName,
            @Value("${download.script.resource.path}") String scriptResourcePath,
            @Value("${download.timeout}") Long downloadTimeoutMillis,
            S3Service s3Service
    ) {
        this.outputPath = outputPath;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.scriptName = scriptName;
        this.scriptResourcePath = scriptResourcePath;
        this.downloadTimeoutMillis = downloadTimeoutMillis;
        this.s3Service = s3Service;
    }

    @Override
    public void download(MusicDownloadDto musicDownloadDto) {
        String youtubeUrl = musicDownloadDto.url();
        // TODO: 1. user validation script. Listen for error stream, if there are any, throw exception
        // if no exception, retrieve dto as response which contains the file size
        // validate file size against threshold
        // then, we can download without worrying about anything
        // and no timeout needed


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

        try (CloseableProcess closeableProcess = new CloseableProcess(processBuilder.start());
             InputStream inputStream = closeableProcess.getInputStream()) {
            startTimeout(closeableProcess.getProcess()).thenAccept(result -> {
                if (!result) {
                    System.out.println("AAAA");
                    System.out.println("AAAA");
                    System.out.println("AAAA");
                    throw new DownloadTimeoutException("Download took too long");
                }
            });

            DownloadResult downloadResult = getDownloadResult(inputStream);
            System.out.println("downloadResult: " + downloadResult);
        } catch (IOException | JsonParseException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        }
    }

    private DownloadResult getDownloadResult(InputStream inputStream) throws IOException, JsonParseException {
        String downloadResultJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
                .lines()
                .filter(this::relatedToJson)
                .collect(Collectors.joining("\n"));

        return new Gson().fromJson(downloadResultJson, DownloadResult.class);
    }

    private CompletableFuture<Boolean> startTimeout(Process process) throws DownloadTimeoutException {
        return CompletableFuture.supplyAsync(() -> {
            boolean downloadedInTime;
            try {
                downloadedInTime = process.waitFor(downloadTimeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return downloadedInTime;
        }).handle((res, ex) -> {
            if (ex != null) {
                return false;
            }
            return res;
        });
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
