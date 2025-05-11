package com.fastify.musicdownload;

import com.fastify.musicdownload.model.dto.MusicDownloadDto;
import com.fastify.musicdownload.service.MusicDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class MusicDownloadApplication {

	private final MusicDownloadService musicDownloadService;

	public static void main(String[] args) {
		SpringApplication.run(MusicDownloadApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			musicDownloadService.download(new MusicDownloadDto("https://www.youtube.com/watch?v=xiWOQaqYyiE"));
		};
	}
}
