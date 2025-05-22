package com.fastify.user.service.handler;

import com.fastify.user.exception.BadPayloadException;
import com.fastify.user.model.entity.User;
import com.fastify.user.model.enumeration.Role;
import com.fastify.user.model.event.UserCreatedEvent;
import com.fastify.user.repository.UserRepository;
import com.fastify.user.util.JsonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandHandlerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JsonConverter jsonConverter;

    @InjectMocks
    CreateUserCommandHandler createUserCommandHandler;

    @Test
    void handle_WhenValidPayloadProvided_ShouldSaveUser() {
        String payload = "payload";
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(1L, "email", Role.USER);
        User user = new User(userCreatedEvent.userId());

        when(jsonConverter.convert(payload, UserCreatedEvent.class))
                .thenReturn(userCreatedEvent);

        createUserCommandHandler.handle(payload);

        verify(userRepository).save(user);
    }

    @Test
    void handle_WhenInvalidPayloadProvided_ShouldThrowBadPayloadException() {
        String payload = "payload";

        when(jsonConverter.convert(payload, UserCreatedEvent.class))
                .thenThrow(new BadPayloadException());

        assertThrows(
                BadPayloadException.class,
                () -> createUserCommandHandler.handle(payload)
        );
        verify(userRepository, never()).save(any());
    }
}
