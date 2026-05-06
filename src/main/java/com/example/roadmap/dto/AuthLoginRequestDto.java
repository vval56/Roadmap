package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Login request payload")
public class AuthLoginRequestDto {

  @NotBlank
  @Size(max = 80)
  @Schema(description = "Login value", example = "admin")
  private String login;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "Password value", example = "admin123")
  private String password;
}
