package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Registration request payload")
public class AuthRegisterRequestDto {

  @NotBlank
  @Size(max = 120)
  @Schema(description = "First name", example = "John")
  private String firstName;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "Last name", example = "Doe")
  private String lastName;

  @NotBlank
  @Email
  @Size(max = 160)
  @Schema(description = "Email address", example = "john.doe@example.com")
  private String email;

  @NotBlank
  @Size(max = 80)
  @Schema(description = "Login value", example = "john")
  private String login;

  @NotBlank
  @Size(min = 4, max = 120)
  @Schema(description = "Password value", example = "john1234")
  private String password;
}
