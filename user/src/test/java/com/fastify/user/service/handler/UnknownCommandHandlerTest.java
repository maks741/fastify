package com.fastify.user.service.handler;

import com.fastify.user.exception.UnknownCommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UnknownCommandHandlerTest {

    @InjectMocks
    UnknownCommandHandler unknownCommandHandler;

    @Test
    void handle_ShouldThrowUnknownCommandException() {
        String payload = "payload";

        assertThrows(
                UnknownCommandException.class,
                () -> unknownCommandHandler.handle(payload)
        );
    }

}
