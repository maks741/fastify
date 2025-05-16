package com.fastify.upload.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastify.upload.exception.BadPayloadException;
import com.google.gson.JsonParseException;
import org.springframework.stereotype.Component;

@Component
public class JsonConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T convert(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException | JsonParseException e) {
            throw new BadPayloadException("Could not convert \n %s \n to %s".formatted(json, clazz.getName()));
        }
    }
}
