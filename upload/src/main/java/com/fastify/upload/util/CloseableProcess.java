package com.fastify.upload.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor
@Getter
public class CloseableProcess implements AutoCloseable {

    private final Process process;

    public InputStream getInputStream() {
        return process.getInputStream();
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }

    @Override
    public void close() {
        process.destroyForcibly();
    }
}
