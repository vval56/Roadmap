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
@Schema(description = "User payload")
public class UserDto {

  @Schema(description = "User identifier", example = "1")
  private Long id;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "First name", example = "Vladislav")
  private String firstName;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "Last name", example = "Mogilny")
  private String lastName;

  @Email
  @NotBlank
  @Size(max = 160)
  @Schema(description = "Email address", example = "vladislav@example.com")
  private String email;
}
