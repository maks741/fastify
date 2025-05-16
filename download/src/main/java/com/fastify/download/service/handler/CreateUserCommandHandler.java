package com.fastify.download.service.handler;

import com.fastify.download.model.entity.User;
import com.fastify.download.model.event.UserCreatedEvent;
import com.fastify.download.repository.UserRepository;
import com.fastify.download.util.JsonConverter;
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
