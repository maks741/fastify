package com.fastify.musicdownload.controller;

import com.fastify.musicdownload.annotation.CurrentUserClaims;
import com.fastify.musicdownload.model.dto.DownloadResultDto;
import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.fastify.musicdownload.model.entity.User;
import com.fastify.musicdownload.service.MusicDownloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class MusicDownloadController {

    private final MusicDownloadService musicDownloadService;

    @PostMapping
    public ResponseEntity<DownloadResultDto> download(
            @CurrentUserClaims User user,
            @RequestBody @Valid MusicDownloadDto musicDownloadDto
    ) {
        return ResponseEntity.ok().body(musicDownloadService.download(user, musicDownloadDto));
    }

}
