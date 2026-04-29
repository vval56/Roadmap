package com.example.roadmap.controller;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import com.example.roadmap.exception.ApiErrorResponse;
import com.example.roadmap.service.RoadMapItemBulkAsyncTaskService;
import com.example.roadmap.service.RoadMapAnalyticsTaskService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async-tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Async Tasks", description = "Async roadmap analytics generation, roadmap item bulk creation and task status tracking")
public class AsyncTaskController {

  private static final String SUBMISSION_RESPONSE_EXAMPLE = """
      {
        "taskId": "91e0c13b-81d3-43a0-b687-dc25c6cb9497",
        "taskType": "ROADMAP_ANALYTICS_REPORT",
        "status": "PENDING",
        "statusEndpoint": "/api/async-tasks/91e0c13b-81d3-43a0-b687-dc25c6cb9497"
      }
      """;

  private static final String BULK_SUBMISSION_RESPONSE_EXAMPLE = """
      {
        "taskId": "76f99f80-38af-47e9-a38c-50191511c4fe",
        "taskType": "ROADMAP_ITEM_BULK_CREATE",
        "status": "PENDING",
        "statusEndpoint": "/api/async-tasks/76f99f80-38af-47e9-a38c-50191511c4fe"
      }
      """;

  private static final String COMPLETED_STATUS_EXAMPLE = """
      {
        "taskId": "91e0c13b-81d3-43a0-b687-dc25c6cb9497",
        "taskType": "ROADMAP_ANALYTICS_REPORT",
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

  private static final String BULK_COMPLETED_STATUS_EXAMPLE = """
      {
        "taskId": "76f99f80-38af-47e9-a38c-50191511c4fe",
        "taskType": "ROADMAP_ITEM_BULK_CREATE",
        "roadMapId": 32,
        "status": "COMPLETED",
        "createdAt": "2026-04-08T12:40:00+03:00",
        "startedAt": "2026-04-08T12:40:01+03:00",
        "completedAt": "2026-04-08T12:40:06+03:00",
        "bulkResult": {
          "roadMapId": 32,
          "createdItemsCount": 2,
          "createdItemIds": [101, 102],
          "finishedAt": "2026-04-08T12:40:06+03:00"
        }
      }
      """;

  private static final String ANALYTICS_FAILED_STATUS_EXAMPLE = """
      {
        "taskId": "568c9d13-5b48-45bf-802a-6bea35ccc3fd",
        "taskType": "ROADMAP_ANALYTICS_REPORT",
        "roadMapId": 999,
        "status": "FAILED",
        "createdAt": "2026-04-27T13:44:54.515141+03:00",
        "startedAt": "2026-04-27T13:44:54.558286+03:00",
        "completedAt": "2026-04-27T13:45:09.632638+03:00",
        "errorMessage": "RoadMap with id=999 not found"
      }
      """;

  private static final String TASK_NOT_FOUND_ERROR_EXAMPLE = """
      {
        "timestamp": "2026-04-27T13:50:00+03:00",
        "status": 404,
        "error": "Not Found",
        "message": "Async task with id=91e0c13b-81d3-43a0-b687-dc25c6cb9497 not found",
        "path": "/api/async-tasks/91e0c13b-81d3-43a0-b687-dc25c6cb9497"
      }
      """;

  private static final String ROADMAP_NOT_FOUND_ERROR_EXAMPLE = """
      {
        "timestamp": "2026-04-27T13:50:00+03:00",
        "status": 404,
        "error": "Not Found",
        "message": "RoadMap with id=1 not found",
        "path": "/api/async-tasks/roadmaps/1/analytics-report"
      }
      """;

  private static final String BULK_REQUEST_EXAMPLE = """
      [
        {
          "title": "Async bulk item",
          "details": "Created from async bulk task",
          "status": "PLANNED",
          "tagIds": []
        },
        {
          "title": "Async bulk item",
          "details": "Same title is allowed, there is no unique constraint",
          "status": "IN_PROGRESS",
          "tagIds": []
        }
      ]
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
  private final RoadMapItemBulkAsyncTaskService roadMapItemBulkAsyncTaskService;

  @PostMapping("/roadmaps/{roadMapId}/analytics-report")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Start async roadmap analytics report generation",
      description = "Creates a background task that calculates analytics for one roadmap and returns task id immediately. "
          + "Use an existing roadmap id from GET /api/roadmaps/catalog or GET /api/roadmaps. "
          + "HTTP 202 means task was accepted, not completed yet. "
          + "If roadmap does not exist, API returns 404 and task is not created.")
  @ApiResponse(responseCode = "202", description = "Async task accepted for background execution",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskSubmissionDto.class),
          examples = @ExampleObject(
              name = "AsyncTaskAccepted",
              value = SUBMISSION_RESPONSE_EXAMPLE)))
  @ApiResponse(responseCode = "400", description = "Invalid roadmap id", content = @Content)
  @ApiResponse(responseCode = "404", description = "Roadmap id not found",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class),
          examples = @ExampleObject(
              name = "RoadMapNotFound",
              value = ROADMAP_NOT_FOUND_ERROR_EXAMPLE)))
  public AsyncTaskSubmissionDto startRoadMapAnalyticsReport(
      @Parameter(description = "Existing roadmap id for which analytics report should be generated. "
          + "Take it from GET /api/roadmaps/catalog response.", example = "2")
      @PathVariable @Positive Long roadMapId) {
    return roadMapAnalyticsTaskService.submitRoadMapReport(roadMapId);
  }

  @PostMapping("/roadmaps/{roadMapId}/bulk-roadmap-items")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Start async roadmap item bulk creation",
      description = "Creates roadmap items in background for one roadmap and returns task id immediately. "
          + "Use an existing roadmap id from GET /api/roadmaps/catalog or GET /api/roadmaps. "
          + "Duplicate titles are allowed because RoadMapItem has no unique constraint on title.")
  @ApiResponse(responseCode = "202", description = "Async bulk create task accepted",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskSubmissionDto.class),
          examples = @ExampleObject(
              name = "AsyncBulkTaskAccepted",
              value = BULK_SUBMISSION_RESPONSE_EXAMPLE)))
  @ApiResponse(responseCode = "400", description = "Invalid roadmap id or invalid bulk payload", content = @Content)
  @ApiResponse(responseCode = "404", description = "Roadmap id not found",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class),
          examples = @ExampleObject(
              name = "RoadMapNotFound",
              value = ROADMAP_NOT_FOUND_ERROR_EXAMPLE)))
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      description = "Bulk array of roadmap items. Duplicate titles are allowed.",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = RoadMapItemBulkCreateDto.class)),
          examples = @ExampleObject(
              name = "AsyncBulkRoadMapItems",
              value = BULK_REQUEST_EXAMPLE)))
  public AsyncTaskSubmissionDto startAsyncRoadMapItemBulkCreate(
      @Parameter(description = "Existing roadmap id that will own all created items. "
          + "Take it from GET /api/roadmaps/catalog response.", example = "32")
      @PathVariable @Positive Long roadMapId,
      @Valid @RequestBody @NotEmpty List<@Valid RoadMapItemBulkCreateDto> dtos) {
    return roadMapItemBulkAsyncTaskService.submitBulkCreate(roadMapId, dtos);
  }

  @GetMapping("/{taskId}")
  @Operation(summary = "Get async task status",
      description = "Use taskId returned by async submission endpoints. "
          + "Task ids are stored in-memory and become unavailable after application restart.")
  @ApiResponse(responseCode = "200", description = "Current task status returned",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = AsyncTaskStatusDto.class),
          examples = {
              @ExampleObject(
                  name = "AnalyticsCompletedStatus",
                  value = COMPLETED_STATUS_EXAMPLE),
              @ExampleObject(
                  name = "BulkCompletedStatus",
                  value = BULK_COMPLETED_STATUS_EXAMPLE),
              @ExampleObject(
                  name = "AnalyticsFailedBecauseRoadmapMissing",
                  value = ANALYTICS_FAILED_STATUS_EXAMPLE)
          }))
  @ApiResponse(responseCode = "404", description = "Task id not found",
      content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class),
          examples = @ExampleObject(
              name = "TaskNotFound",
              value = TASK_NOT_FOUND_ERROR_EXAMPLE)))
  public AsyncTaskStatusDto getTaskStatus(
      @Parameter(description = "Async task id returned by POST /api/async-tasks/... endpoints",
          example = "91e0c13b-81d3-43a0-b687-dc25c6cb9497")
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
