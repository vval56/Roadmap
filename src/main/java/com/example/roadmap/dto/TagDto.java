package com.example.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TagDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class TagDto {

  private Long id;

  @NotBlank
  private String name;
}
