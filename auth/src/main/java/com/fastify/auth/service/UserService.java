package com.fastify.auth.service;

import com.fastify.auth.model.dto.SignUpRequestDto;
import com.fastify.auth.model.dto.SignUpResponseDto;

public interface UserService {

    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);

}
