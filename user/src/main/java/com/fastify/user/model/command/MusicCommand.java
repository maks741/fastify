package com.fastify.user.model.command;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MusicCommand {
    DOWNLOAD,
    LISTEN,
    DELETE,
    UNKNOWN;

    public static MusicCommand fromString(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            log.warn("Received unknown command: {}", s);
            return UNKNOWN;
        }
    }
}
