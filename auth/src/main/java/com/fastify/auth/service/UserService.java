package com.fastify.auth.service;

import com.fastify.auth.model.dto.LoginRequestDto;
import com.fastify.auth.model.dto.LoginResponseDto;
import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;
import com.fastify.auth.model.entity.User;

public interface UserService {

    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    User findByEmail(String email);

}
