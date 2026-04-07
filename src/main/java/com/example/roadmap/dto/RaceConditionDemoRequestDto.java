package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request payload for race condition demonstration")
public class RaceConditionDemoRequestDto {

  @Min(50)
  @Max(500)
  @Schema(description = "How many concurrent worker threads to start", example = "64")
  private int threadCount = 64;

  @Min(100)
  @Max(20000)
  @Schema(description = "How many increments each thread performs", example = "5000")
  private int incrementsPerThread = 5000;
}
