package com.fastify.musicdownload.service;

import com.fastify.musicdownload.model.dto.MusicDownloadDto;

public interface MusicDownloadService {

    void download(MusicDownloadDto musicDownloadDto);

}
