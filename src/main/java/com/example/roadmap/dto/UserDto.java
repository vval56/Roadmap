package com.example.roadmap.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * UserDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto {

  private Long id;

  @NotBlank
  private String fullName;

  @Email
  @NotBlank
  private String email;
}
