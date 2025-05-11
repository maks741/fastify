package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.DownloadTimeoutException;
import com.fastify.musicdownload.exception.InvalidUrlException;
import com.fastify.musicdownload.exception.UnableToDownloadException;
import com.fastify.musicdownload.model.DownloadResult;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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

            new FutureTask<>(() -> {
                boolean downloadedInTime;
                try {
                    downloadedInTime = process.waitFor(downloadTimeoutMillis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!downloadedInTime) {
                    // TODO: there are leftovers from download. Need a way to erase them
                    process.destroyForcibly();
                    throw new DownloadTimeoutException("Download took too long");
                }
                return downloadedInTime;
            }).run();

            DownloadResult downloadResult = CompletableFuture.supplyAsync(() -> {
                try {
                    return getDownloadResult(process);
                } catch (IOException e) {
                    throw new UnableToDownloadException("Unable to download");
                }
            }).join();
            System.out.println("downloadResult: " + downloadResult);
        } catch (IOException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        }
    }

    private DownloadResult getDownloadResult(Process process) throws IOException {
        StringBuilder downloadResultJsonBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (unrelatedToJson(line)) {
                continue;
            }
            downloadResultJsonBuilder.append(line);
        }
        String downloadResultJson = downloadResultJsonBuilder.toString();
        return new Gson().fromJson(downloadResultJson, DownloadResult.class);
    }

    private void validateYoutubeUrl(String url) {
        String regex = "^http[s,]://.*youtube.*";
        if (!url.matches(regex)) {
            throw new InvalidUrlException(url + " is not a valid Youtube url");
        }
    }

    private boolean unrelatedToJson(String line) {
        String trimmed = line.trim();
        return !(trimmed.startsWith("{") || trimmed.startsWith("}") || trimmed.startsWith("\""));
    }
}
