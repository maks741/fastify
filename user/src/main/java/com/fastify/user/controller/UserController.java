package com.fastify.user.controller;

import com.fastify.user.annotation.CurrentUserClaims;
import com.fastify.user.model.dto.user.UserClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<UserClaims> test(@CurrentUserClaims UserClaims userClaims) {
        return ResponseEntity.ok().body(userClaims);
    }

}
