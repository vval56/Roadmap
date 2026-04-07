package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.RoadMapAnalyticsReportDto;
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
    assertEquals(2L, status.getRoadMapId());
    assertNotNull(status.getStartedAt());
    assertNotNull(status.getCompletedAt());
    assertNotNull(status.getReport());
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
    assertEquals("RoadMap with id=999 not found", status.getErrorMessage());
    assertNull(status.getReport());
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
}
