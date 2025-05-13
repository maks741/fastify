package com.fastify.musicdownload.service;

import com.fastify.musicdownload.model.dto.DownloadResultDto;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.fastify.musicdownload.model.entity.User;

public interface MusicDownloadService {

    DownloadResultDto download(User user, MusicDownloadDto musicDownloadDto);

}
