package com.fastify.musicdownload.service;

import com.fastify.musicdownload.exception.DownloadTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Service
public class DownloadTimeoutService {

    private final Long downloadTimeoutMillis;

    public DownloadTimeoutService(
            @Value("${download.timeout}") Long downloadTimeoutMillis
    ) {
        this.downloadTimeoutMillis = downloadTimeoutMillis;
    }

    public void startTimeout(Process process) throws DownloadTimeoutException {
        new FutureTask<>(() -> {
            boolean downloadedInTime;
            downloadedInTime = process.waitFor(downloadTimeoutMillis, TimeUnit.MILLISECONDS);

            if (!downloadedInTime) {
                // TODO: there are leftovers from download. Need a way to erase them
                process.destroyForcibly();
                throw new DownloadTimeoutException("Download took too long");
            }
            return downloadedInTime;
        }).run();
    }
}
