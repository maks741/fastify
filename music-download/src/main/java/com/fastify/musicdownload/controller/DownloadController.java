package com.fastify.musicdownload.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadController {

    @GetMapping
    public String download() {
        return "lox";
    }

}
