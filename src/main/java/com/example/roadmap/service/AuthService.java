package com.example.roadmap.service;

import com.example.roadmap.dto.AuthLoginRequestDto;
import com.example.roadmap.dto.AuthLoginResponseDto;
import com.example.roadmap.dto.AuthRegisterRequestDto;

public interface AuthService {

  AuthLoginResponseDto login(AuthLoginRequestDto request);

  AuthLoginResponseDto register(AuthRegisterRequestDto request);
}
