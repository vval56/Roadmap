package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncRoadMapItemBulkResultDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskType;
import com.example.roadmap.dto.RoadMapAnalyticsReportDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class AsyncTaskRegistryServiceTest {

  private final AsyncTaskRegistryService asyncTaskRegistryService = new AsyncTaskRegistryService();

  @Test
  void shouldTrackCompletedAsyncTaskAndCounters() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(2L);

    asyncTaskRegistryService.markRunning(taskId);

    RoadMapAnalyticsReportDto report = new RoadMapAnalyticsReportDto();
    report.setRoadMapId(2L);
    report.setRoadMapTitle("Java Backend Roadmap");
    report.setGeneratedAt(OffsetDateTime.now());
    asyncTaskRegistryService.complete(taskId, report);

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.COMPLETED, status.getStatus());
    assertEquals(AsyncTaskType.ROADMAP_ANALYTICS_REPORT, status.getTaskType());
    assertEquals(2L, status.getRoadMapId());
    assertNotNull(status.getStartedAt());
    assertNotNull(status.getCompletedAt());
    assertNotNull(status.getReport());
    assertNull(status.getBulkResult());
    assertEquals(1L, counters.getSubmittedTasks());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(1L, counters.getCompletedTasks());
    assertEquals(0L, counters.getFailedTasks());
  }

  @Test
  void shouldTrackFailedAsyncTaskAndCounters() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(999L);

    asyncTaskRegistryService.markRunning(taskId);
    asyncTaskRegistryService.fail(taskId, "RoadMap with id=999 not found");

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.FAILED, status.getStatus());
    assertEquals(AsyncTaskType.ROADMAP_ANALYTICS_REPORT, status.getTaskType());
    assertEquals("RoadMap with id=999 not found", status.getErrorMessage());
    assertNull(status.getReport());
    assertNull(status.getBulkResult());
    assertEquals(1L, counters.getSubmittedTasks());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(0L, counters.getCompletedTasks());
    assertEquals(1L, counters.getFailedTasks());
  }

  @Test
  void shouldNotMakeRunningCounterNegativeWhenTaskFinishesWithoutExplicitRunningState() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(2L);

    RoadMapAnalyticsReportDto report = new RoadMapAnalyticsReportDto();
    report.setRoadMapId(2L);
    report.setGeneratedAt(OffsetDateTime.now());
    asyncTaskRegistryService.complete(taskId, report);

    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(0L, counters.getRunningTasks());
    assertEquals(1L, counters.getCompletedTasks());
    assertEquals(0L, counters.getFailedTasks());
  }

  @Test
  void shouldNotDoubleCountWhenTaskIsMarkedRunningOrCompletedTwice() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(3L);
    RoadMapAnalyticsReportDto report = new RoadMapAnalyticsReportDto();
    report.setRoadMapId(3L);

    asyncTaskRegistryService.markRunning(taskId);
    asyncTaskRegistryService.markRunning(taskId);
    asyncTaskRegistryService.complete(taskId, report);
    asyncTaskRegistryService.complete(taskId, report);
    asyncTaskRegistryService.fail(taskId, "ignored");

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.COMPLETED, status.getStatus());
    assertEquals(AsyncTaskType.ROADMAP_ANALYTICS_REPORT, status.getTaskType());
    assertEquals(1L, counters.getSubmittedTasks());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(1L, counters.getCompletedTasks());
    assertEquals(0L, counters.getFailedTasks());
  }

  @Test
  void shouldNotChangeCountersWhenFailedTaskIsFinishedAgain() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(5L);
    RoadMapAnalyticsReportDto report = new RoadMapAnalyticsReportDto();
    report.setRoadMapId(5L);

    asyncTaskRegistryService.markRunning(taskId);
    asyncTaskRegistryService.fail(taskId, "Initial failure");
    asyncTaskRegistryService.complete(taskId, report);
    asyncTaskRegistryService.fail(taskId, "Ignored failure");

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.FAILED, status.getStatus());
    assertEquals("Initial failure", status.getErrorMessage());
    assertNull(status.getReport());
    assertNull(status.getBulkResult());
    assertEquals(1L, counters.getSubmittedTasks());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(0L, counters.getCompletedTasks());
    assertEquals(1L, counters.getFailedTasks());
  }

  @Test
  void shouldFailPendingTaskWithoutRunningState() {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(4L);

    asyncTaskRegistryService.fail(taskId, "Failed before worker start");

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.FAILED, status.getStatus());
    assertEquals(AsyncTaskType.ROADMAP_ANALYTICS_REPORT, status.getTaskType());
    assertNotNull(status.getStartedAt());
    assertNotNull(status.getCompletedAt());
    assertEquals("Failed before worker start", status.getErrorMessage());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(0L, counters.getCompletedTasks());
    assertEquals(1L, counters.getFailedTasks());
  }

  @Test
  void shouldThrowWhenTaskDoesNotExist() {
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> asyncTaskRegistryService.getStatus("report-9999"));

    assertEquals("Async task with id=report-9999 not found", exception.getMessage());
  }

  @Test
  void shouldTrackCompletedAsyncBulkTaskAndCounters() {
    String taskId = asyncTaskRegistryService.registerRoadMapItemBulkTask(32L);

    asyncTaskRegistryService.markRunning(taskId);

    AsyncRoadMapItemBulkResultDto bulkResult = new AsyncRoadMapItemBulkResultDto();
    bulkResult.setRoadMapId(32L);
    bulkResult.setCreatedItemsCount(2);
    bulkResult.setCreatedItemIds(java.util.List.of(101L, 102L));
    bulkResult.setFinishedAt(OffsetDateTime.now());
    asyncTaskRegistryService.completeBulk(taskId, bulkResult);

    AsyncTaskStatusDto status = asyncTaskRegistryService.getStatus(taskId);
    AsyncTaskCountersDto counters = asyncTaskRegistryService.getCounters();

    assertEquals(AsyncTaskStatus.COMPLETED, status.getStatus());
    assertEquals(AsyncTaskType.ROADMAP_ITEM_BULK_CREATE, status.getTaskType());
    assertEquals(32L, status.getRoadMapId());
    assertNull(status.getReport());
    assertNotNull(status.getBulkResult());
    assertEquals(2, status.getBulkResult().getCreatedItemsCount());
    assertEquals(java.util.List.of(101L, 102L), status.getBulkResult().getCreatedItemIds());
    assertEquals(1L, counters.getSubmittedTasks());
    assertEquals(0L, counters.getRunningTasks());
    assertEquals(1L, counters.getCompletedTasks());
    assertEquals(0L, counters.getFailedTasks());
  }
}
