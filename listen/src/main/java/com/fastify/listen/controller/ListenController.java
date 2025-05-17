package com.fastify.listen.controller;

import com.fastify.listen.annotation.CurrentUserClaims;
import com.fastify.listen.model.dto.user.UserClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/listen")
@RequiredArgsConstructor
public class ListenController {

    @GetMapping
    public ResponseEntity<UserClaims> listen(@CurrentUserClaims UserClaims userClaims) {
        return ResponseEntity.ok(userClaims);
    }
}
