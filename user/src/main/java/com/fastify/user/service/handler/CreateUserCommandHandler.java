package com.fastify.user.service.handler;

import com.fastify.user.model.entity.User;
import com.fastify.user.model.event.UserCreatedEvent;
import com.fastify.user.repository.UserRepository;
import com.fastify.user.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler implements CommandHandler {

    private final JsonConverter jsonConverter;
    private final UserRepository userRepository;

    @Override
    public void handle(String payload) {
        UserCreatedEvent userCreatedEvent = jsonConverter.convert(payload, UserCreatedEvent.class);
        User user = new User(userCreatedEvent.userId());
        userRepository.save(user);
    }
}
