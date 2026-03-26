package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Roadmap payload")
public class RoadMapDto {

  @Schema(description = "Roadmap identifier", example = "1")
  private Long id;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "Roadmap title", example = "Java Backend 2026")
  private String title;

  @Size(max = 500)
  @Schema(description = "Roadmap description", example = "Learning plan for Java, Spring and PostgreSQL")
  private String description;

  @NotNull
  @Positive
  @Schema(description = "Owner user identifier", example = "1")
  private Long ownerId;
}
