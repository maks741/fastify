package com.fastify.download.controller;

import com.fastify.download.annotation.CurrentUser;
import com.fastify.download.model.dto.DownloadResultDto;
import com.fastify.download.model.dto.MusicDownloadDto;
import com.fastify.download.model.entity.User;
import com.fastify.download.service.MusicDownloadService;
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
