package com.fastify.download.service;

import com.fastify.download.model.dto.DownloadResultDto;
import com.fastify.download.model.dto.MusicDownloadDto;
import com.fastify.download.model.entity.User;

public interface MusicDownloadService {

    DownloadResultDto download(User user, MusicDownloadDto musicDownloadDto);

}
