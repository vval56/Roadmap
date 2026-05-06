package com.example.roadmap.controller;

import com.example.roadmap.dto.AuthLoginRequestDto;
import com.example.roadmap.dto.AuthLoginResponseDto;
import com.example.roadmap.dto.AuthRegisterRequestDto;
import com.example.roadmap.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  @Operation(summary = "Login with login and password")
  public AuthLoginResponseDto login(@Valid @RequestBody AuthLoginRequestDto request) {
    return authService.login(request);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Register a new user with login and password")
  public AuthLoginResponseDto register(@Valid @RequestBody AuthRegisterRequestDto request) {
    return authService.register(request);
  }
}
