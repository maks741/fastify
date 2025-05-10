package com.fastify.auth.service;

import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;
import com.fastify.auth.model.entity.User;
import com.fastify.auth.model.enumeration.Role;
import com.fastify.auth.repository.UserRepository;
import com.fastify.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = User.builder()
                .username(signUpRequestDto.username())
                .email(signUpRequestDto.email())
                .password(passwordEncoder.encode(signUpRequestDto.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        String jwt = jwtService.generateToken(user);

        return new SignUpResponseDto(
                user.getId(),
                jwt,
                user.getUsername(),
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
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(RuntimeException::new);
    }
}
