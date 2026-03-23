package com.example.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoadMapDto {

  private Long id;

  @NotBlank
  private String title;

  private String description;

  @NotNull
  private Long ownerId;
}
