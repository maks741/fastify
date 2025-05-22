package com.fastify.user.service;

import com.fastify.user.exception.UserNotFoundException;
import com.fastify.user.model.entity.User;
import com.fastify.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void findById_WhenUserIsFound_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User actualResult = userService.findById(userId);

        assertEquals(user, actualResult);
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(userId)
        );
    }
}
