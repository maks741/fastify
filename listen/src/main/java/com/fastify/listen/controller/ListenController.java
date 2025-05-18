package com.fastify.listen.controller;

import com.fastify.listen.annotation.CurrentUserClaims;
import com.fastify.listen.model.dto.ListenResponse;
import com.fastify.listen.model.dto.user.UserClaims;
import com.fastify.listen.service.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/listen")
@RequiredArgsConstructor
public class ListenController {

    private final FileStorage fileStorage;

    @GetMapping("/{video-id}")
    public ResponseEntity<ListenResponse> listen(
            @CurrentUserClaims UserClaims userClaims,
            @PathVariable("video-id") String videoId
    ) {
        return ResponseEntity.ok(fileStorage.generateAudioUrl(userClaims, videoId));
    }
}
