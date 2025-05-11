package com.fastify.musicdownload.service;

import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class YoutubeDownloadService implements MusicDownloadService {

    @Value("${download.out.path}")
    private String outputPath;

    @Value("${download.out.format.audio}")
    private String audioFormat;

    @Value("${download.out.format.img}")
    private String thumbnailFormat;

    @Value("${download.script.run}")
    private String scriptName;

    @Value("${download.script.resource.path}")
    private String scriptResourcePath;

    @Override
    public void download(MusicDownloadDto musicDownloadDto) {
        String youtubeUrl = musicDownloadDto.url();
        String scriptsDirPath = Objects.requireNonNull(getClass().getResource(scriptResourcePath)).getPath();

        // TODO: validate youtube url

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
            process.waitFor(15, TimeUnit.SECONDS);
        } catch (IOException e) {
            // TODO: custom exception
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
