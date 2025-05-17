package com.fastify.user.controller;

import com.fastify.user.annotation.CurrentUser;
import com.fastify.user.model.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<User> test(@CurrentUser User user) {
        return ResponseEntity.ok().body(user);
    }

}
