package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.DownloadTimeoutException;
import com.fastify.musicdownload.exception.InvalidUrlException;
import com.fastify.musicdownload.exception.UnableToDownloadException;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class YoutubeDownloadService implements MusicDownloadService {

    private final String outputPath;
    private final String audioFormat;
    private final String thumbnailFormat;
    private final String scriptName;
    private final String scriptResourcePath;
    private final Long downloadTimeoutMillis;

    public YoutubeDownloadService(
            @Value("${download.out.path}") String outputPath,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.img}") String thumbnailFormat,
            @Value("${download.script.run}") String scriptName,
            @Value("${download.script.resource.path}") String scriptResourcePath,
            @Value("${download.timeout}") Long downloadTimeoutMillis
    ) {
        this.outputPath = outputPath;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.scriptName = scriptName;
        this.scriptResourcePath = scriptResourcePath;
        this.downloadTimeoutMillis = downloadTimeoutMillis;
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
            boolean downloadedInTime = process.waitFor(downloadTimeoutMillis, TimeUnit.MILLISECONDS);

            if (!downloadedInTime) {
                // TODO: there are leftovers from download. Need a way to erase them
                process.destroyForcibly();
                throw new DownloadTimeoutException("Download took too long");
            }
        } catch (IOException e) {
            throw new UnableToDownloadException("Unexpected error during download");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateYoutubeUrl(String url) {
        String regex = "^http[s,]://.*youtube.*";
        if (!url.matches(regex)) {
            throw new InvalidUrlException(url + " is not a valid Youtube url");
        }
    }
}
