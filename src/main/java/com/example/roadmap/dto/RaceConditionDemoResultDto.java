package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Result payload that shows race condition and safe counter solutions")
public class RaceConditionDemoResultDto {

  @Schema(description = "Number of worker threads used in the demo", example = "64")
  private int threadCount;

  @Schema(description = "How many increments were requested per thread", example = "5000")
  private int incrementsPerThread;

  @Schema(description = "Mathematically expected final counter value", example = "320000")
  private int expectedValue;

  @Schema(description = "Value produced by unsafe counter with race condition", example = "241532")
  private int unsafeCounterValue;

  @Schema(description = "Value produced by synchronized counter", example = "320000")
  private int synchronizedCounterValue;

  @Schema(description = "Value produced by AtomicInteger counter", example = "320000")
  private int atomicCounterValue;

  @Schema(description = "Whether the race condition was actually observed", example = "true")
  private boolean raceConditionDetected;

  @Schema(description = "Execution time in milliseconds", example = "184")
  private long durationMs;
}
