package com.fastify.upload.service.handler;

import com.fastify.upload.model.entity.User;
import com.fastify.upload.model.event.UserCreatedEvent;
import com.fastify.upload.repository.UserRepository;
import com.fastify.upload.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler implements UserCommandHandler {

    private final UserRepository userRepository;
    private final JsonConverter jsonConverter;

    @Override
    public void handle(String payload) {
        UserCreatedEvent userCreatedEvent = jsonConverter.convert(payload, UserCreatedEvent.class);
        User user = new User(userCreatedEvent.userId());
        userRepository.save(user);
    }
}
