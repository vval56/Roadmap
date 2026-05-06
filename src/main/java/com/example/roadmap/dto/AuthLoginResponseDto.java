package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Login response payload")
public class AuthLoginResponseDto {

  @Schema(description = "Login result message", example = "Signed in")
  private String message;

  @Schema(description = "Authenticated user")
  private UserDto user;
}
