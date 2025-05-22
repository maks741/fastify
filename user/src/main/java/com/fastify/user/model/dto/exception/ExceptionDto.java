package com.fastify.user.model.dto.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ExceptionDto(
        Object payload,
        String timestamp
) {
    public ExceptionDto(Object payload) {
        this(payload, currentTimestamp());
    }

    private static String currentTimestamp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }
}
