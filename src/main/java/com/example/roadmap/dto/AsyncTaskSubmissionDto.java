package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Response returned immediately after async task submission")
public class AsyncTaskSubmissionDto {

  @Schema(description = "Async task identifier", example = "91e0c13b-81d3-43a0-b687-dc25c6cb9497")
  private final String taskId;

  @Schema(description = "Initial task status", example = "PENDING")
  private final AsyncTaskStatus status;

  @Schema(description = "Relative endpoint to poll task status",
      example = "/api/async-tasks/91e0c13b-81d3-43a0-b687-dc25c6cb9497")
  private final String statusEndpoint;
}
