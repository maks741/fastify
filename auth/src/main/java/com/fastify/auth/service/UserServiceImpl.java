package com.fastify.auth.service;

import com.fastify.auth.controller.kafka.UserCommandProducer;
import com.fastify.auth.exception.DuplicateEmailException;
import com.fastify.auth.model.command.UserCommand;
import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;
import com.fastify.auth.model.entity.User;
import com.fastify.auth.model.enumeration.Role;
import com.fastify.auth.model.event.UserCreatedEvent;
import com.fastify.auth.repository.UserRepository;
import com.fastify.auth.security.JwtService;
import com.fastify.auth.util.JsonConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserCommandProducer userCommandProducer;
    private final JsonConverter jsonConverter;
    private final String userCommandTopic;

    public UserServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserCommandProducer userCommandProducer, JsonConverter jsonConverter,
            @Value("${spring.kafka.topics[0].name}") String userCommandTopic) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userCommandProducer = userCommandProducer;
        this.jsonConverter = jsonConverter;
        this.userCommandTopic = userCommandTopic;
    }

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.username();
        User user = User.builder()
                .name(username)
                .email(signUpRequestDto.email())
                .password(passwordEncoder.encode(signUpRequestDto.password()))
                .role(Role.USER)
                .build();

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user.getId(), user.getEmail(), user.getRole());
        userCommandProducer.sendUserCommand(
                userCommandTopic,
                user.getId(),
                jsonConverter.toJson(userCreatedEvent),
                UserCommand.CREATE
        );

        String jwt = jwtService.generateToken(user);

        return new SignUpResponseDto(
                user.getId(),
                jwt,
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        loginRequestDto.password()
                )
        );
        User user = findByEmail(email);
        String jwt = jwtService.generateToken(user);

        return new LoginResponseDto(
                user.getId(),
                jwt,
                user.getName(),
                user.getEmail()
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(RuntimeException::new);
    }
}
