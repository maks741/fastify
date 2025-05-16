package com.fastify.upload.model.command;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum UserCommand {
    CREATE,
    UNKNOWN;

    public static UserCommand fromString(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            log.warn("Received unknown command: {}", e.getMessage());
            return UNKNOWN;
        }
    }
}
