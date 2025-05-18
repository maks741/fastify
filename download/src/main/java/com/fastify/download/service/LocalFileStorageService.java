package com.fastify.download.service;

import com.fastify.download.model.DownloadResult;
import com.fastify.download.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Profile("dev")
public class LocalFileStorageService implements FileStorage {

    private final String storagePath;
    private final String audioSuffix;
    private final String thumbnailSuffix;
    private final String audioFormat;
    private final String thumbnailFormat;
    private final Environment environment;

    public LocalFileStorageService(
            @Value("${download.storage.path}") String storagePath,
            @Value("${aws.objects.suffix.audio}") String audioSuffix,
            @Value("${aws.objects.suffix.thumbnail}") String thumbnailSuffix,
            @Value("${download.out.format.audio}") String audioFormat,
            @Value("${download.out.format.thumbnail}") String thumbnailFormat,
            Environment environment
    ) {
        this.storagePath = storagePath;
        this.audioSuffix = audioSuffix;
        this.thumbnailSuffix = thumbnailSuffix;
        this.audioFormat = audioFormat;
        this.thumbnailFormat = thumbnailFormat;
        this.environment = environment;
    }

    @Override
    public void store(Long userId, DownloadResult downloadResult) {
        Path downloadedAudioPath = FileUtils.toExistingPath(downloadResult.audioPath());
        Path downloadedThumbnailPath = FileUtils.toExistingPath(downloadResult.thumbnailPath());
        Path storageFolderPath = FileUtils.toExistingPath(storagePath);

        String relativeStoragePathStr = userId + "/" + downloadResult.videoId() + "/";
        Path relativeStoragePath = Paths.get(relativeStoragePathStr);
        Path storageSubPath = storageFolderPath.resolve(relativeStoragePath);

        Path audioPath = storageSubPath.resolve(Paths.get(audioSuffix + "." + audioFormat));
        Path thumbnailPath = storageSubPath.resolve(Paths.get(thumbnailSuffix + "." + thumbnailFormat));

        FileUtils.createDirectories(storageSubPath);
        FileUtils.createFile(audioPath);
        FileUtils.createFile(thumbnailPath);

        try {
            Files.copy(downloadedAudioPath, audioPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(downloadedThumbnailPath, thumbnailPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateThumbnailUrl(Long userId, String videoId) {
        String storageSubFolderPath = userId + "/" + videoId + "/";
        String thumbnailPathStr = storageSubFolderPath + thumbnailSuffix + "." + thumbnailFormat;

        return buildBaseUrl() + "/resources/" + thumbnailPathStr;
    }

    private String buildBaseUrl() {
        return "http://" + environment.getProperty("server.address") + ":" + environment.getProperty("server.port");
    }
}
