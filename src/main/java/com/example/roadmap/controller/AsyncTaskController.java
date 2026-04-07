package com.example.roadmap.controller;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import com.example.roadmap.service.RoadMapAnalyticsTaskService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async-tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Async Tasks", description = "Async roadmap analytics generation and task status tracking")
public class AsyncTaskController {

  private static final String SUBMISSION_RESPONSE_EXAMPLE = """
      {
        "taskId": "report-1001",
        "status": "PENDING",
        "statusEndpoint": "/api/async-tasks/report-1001"
      }
      """;

  private static final String COMPLETED_STATUS_EXAMPLE = """
      {
        "taskId": "report-1001",
        "roadMapId": 2,
        "status": "COMPLETED",
        "createdAt": "2026-04-07T12:00:00+03:00",
        "startedAt": "2026-04-07T12:00:01+03:00",
        "completedAt": "2026-04-07T12:00:02+03:00",
        "report": {
          "roadMapId": 2,
          "roadMapTitle": "Java Backend Roadmap",
          "ownerEmail": "vladislav@example.com",
          "totalItems": 1,
          "plannedItems": 0,
          "inProgressItems": 1,
          "doneItems": 0,
          "totalComments": 1,
          "completionRatePercent": 0.0,
          "distinctTagNames": ["spring", "sql"],
          "generatedAt": "2026-04-07T12:00:02+03:00"
        }
      }
      """;

  private static final String COUNTERS_RESPONSE_EXAMPLE = """
      {
        "submittedTasks": 3,
        "runningTasks": 1,
        "completedTasks": 1,
        "failedTasks": 1
      }
      """;

  private final RoadMapAnalyticsTaskService roadMapAnalyticsTaskService;

  @PostMapping("/roadmaps/{roadMapId}/analytics-report")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Start async roadmap analytics report generation",
      description = "Creates a background task that calculates analytics for one roadmap and returns task id immediately. "
          + "If roadmap does not exist, task will move to FAILED status and the error can be observed through polling.")
  @ApiResponse(responseCode = "202", description = "Async task accepted for background execution",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskSubmissionDto.class),
          examples = @ExampleObject(
              name = "AsyncTaskAccepted",
              value = SUBMISSION_RESPONSE_EXAMPLE)))
  @ApiResponse(responseCode = "400", description = "Invalid roadmap id", content = @Content)
  public AsyncTaskSubmissionDto startRoadMapAnalyticsReport(
      @Parameter(description = "Roadmap id for which analytics report should be generated", example = "2")
      @PathVariable @Positive Long roadMapId) {
    return roadMapAnalyticsTaskService.submitRoadMapReport(roadMapId);
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get async task status")
  @ApiResponse(responseCode = "200", description = "Current task status returned",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskStatusDto.class),
          examples = @ExampleObject(
              name = "CompletedStatus",
              value = COMPLETED_STATUS_EXAMPLE)))
  @ApiResponse(responseCode = "404", description = "Task id not found", content = @Content)
  public AsyncTaskStatusDto getTaskStatus(
      @Parameter(description = "Async task id", example = "report-1001")
      @PathVariable String taskId) {
    return roadMapAnalyticsTaskService.getTaskStatus(taskId);
  }

  @GetMapping("/metrics")
  @Operation(summary = "Get thread-safe async task counters")
  @ApiResponse(responseCode = "200", description = "Thread-safe counters returned",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskCountersDto.class),
          examples = @ExampleObject(
              name = "Counters",
              value = COUNTERS_RESPONSE_EXAMPLE)))
  public AsyncTaskCountersDto getTaskCounters() {
    return roadMapAnalyticsTaskService.getCounters();
  }
}
