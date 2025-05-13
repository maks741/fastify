package com.fastify.musicdownload.service;

import com.fastify.musicdownload.model.dto.DownloadResultDto;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;

public interface MusicDownloadService {

    DownloadResultDto download(MusicDownloadDto musicDownloadDto);

}
