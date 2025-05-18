package com.fastify.download.util;

import com.fastify.download.exception.FileDoesNotExistException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {
    public static Path toExistingPath(String pathStr) {
        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            throw new FileDoesNotExistException("File does not exist by that path: " + path);
        }
        return path;
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            log.trace("Directory already exists: {}", e.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile(Path path) {
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            log.trace("File already exists: {}", e.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
