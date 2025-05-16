package com.fastify.upload.service;

import com.fastify.upload.model.dto.DownloadResultDto;
import com.fastify.upload.model.dto.MusicDownloadDto;
import com.fastify.upload.model.entity.User;

public interface MusicDownloadService {

    DownloadResultDto download(User user, MusicDownloadDto musicDownloadDto);

}
