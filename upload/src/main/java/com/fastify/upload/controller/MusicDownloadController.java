package com.fastify.upload.controller;

import com.fastify.upload.annotation.CurrentUser;
import com.fastify.upload.model.dto.DownloadResultDto;
import com.fastify.upload.model.dto.MusicDownloadDto;
import com.fastify.upload.model.entity.User;
import com.fastify.upload.service.MusicDownloadService;
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
            @CurrentUser User user,
            @RequestBody @Valid MusicDownloadDto musicDownloadDto
    ) {
        return ResponseEntity.ok().body(musicDownloadService.download(user, musicDownloadDto));
    }

}
