package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Thread-safe counters for async task processing")
public class AsyncTaskCountersDto {

  @Schema(description = "Number of tasks submitted since application start", example = "12")
  private final long submittedTasks;

  @Schema(description = "Number of tasks currently running", example = "1")
  private final long runningTasks;

  @Schema(description = "Number of completed tasks", example = "10")
  private final long completedTasks;

  @Schema(description = "Number of failed tasks", example = "1")
  private final long failedTasks;
}
