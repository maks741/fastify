package com.fastify.user.controller;

import com.fastify.user.annotation.CurrentUserClaims;
import com.fastify.user.model.dto.MusicDto;
import com.fastify.user.model.dto.user.UserClaims;
import com.fastify.user.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final MusicService musicService;

    @GetMapping("/music")
    public ResponseEntity<List<MusicDto>> findAllMusicOfUser(
            @CurrentUserClaims UserClaims userClaims
    ) {
        return ResponseEntity.ok(musicService.findAllByUserId(userClaims));
    }
}
