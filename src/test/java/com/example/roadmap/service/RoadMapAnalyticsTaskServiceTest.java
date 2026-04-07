package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadMapAnalyticsTaskServiceTest {

  private static final String TASK_ID = "91e0c13b-81d3-43a0-b687-dc25c6cb9497";

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @Mock
  private RoadMapAnalyticsAsyncWorker roadMapAnalyticsAsyncWorker;

  @InjectMocks
  private RoadMapAnalyticsTaskService roadMapAnalyticsTaskService;

  @Test
  void submitRoadMapReportShouldRegisterTaskAndStartAsyncWorker() {
    when(asyncTaskRegistryService.registerRoadMapReportTask(2L)).thenReturn(TASK_ID);
    when(roadMapAnalyticsAsyncWorker.generateReportAsync(TASK_ID, 2L))
        .thenReturn(CompletableFuture.completedFuture(null));

    var response = roadMapAnalyticsTaskService.submitRoadMapReport(2L);

    assertEquals(TASK_ID, response.getTaskId());
    assertEquals(AsyncTaskStatus.PENDING, response.getStatus());
    assertEquals("/api/async-tasks/" + TASK_ID, response.getStatusEndpoint());
    verify(asyncTaskRegistryService).registerRoadMapReportTask(2L);
    verify(roadMapAnalyticsAsyncWorker).generateReportAsync(TASK_ID, 2L);
  }

  @Test
  void getTaskStatusShouldDelegateToRegistry() {
    AsyncTaskStatusDto expected = new AsyncTaskStatusDto(
        TASK_ID,
        2L,
        AsyncTaskStatus.RUNNING,
        OffsetDateTime.parse("2026-04-07T12:00:00+03:00"),
        OffsetDateTime.parse("2026-04-07T12:00:01+03:00"),
        null,
        null,
        null);
    when(asyncTaskRegistryService.getStatus(TASK_ID)).thenReturn(expected);

    AsyncTaskStatusDto actual = roadMapAnalyticsTaskService.getTaskStatus(TASK_ID);

    assertEquals(expected.getTaskId(), actual.getTaskId());
    assertEquals(expected.getRoadMapId(), actual.getRoadMapId());
    assertEquals(expected.getStatus(), actual.getStatus());
    assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
    assertEquals(expected.getStartedAt(), actual.getStartedAt());
    assertEquals(expected.getCompletedAt(), actual.getCompletedAt());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
    assertEquals(expected.getReport(), actual.getReport());
  }

  @Test
  void getCountersShouldDelegateToRegistry() {
    AsyncTaskCountersDto expected = new AsyncTaskCountersDto(7, 2, 4, 1);
    when(asyncTaskRegistryService.getCounters()).thenReturn(expected);

    AsyncTaskCountersDto actual = roadMapAnalyticsTaskService.getCounters();

    assertEquals(expected.getSubmittedTasks(), actual.getSubmittedTasks());
    assertEquals(expected.getRunningTasks(), actual.getRunningTasks());
    assertEquals(expected.getCompletedTasks(), actual.getCompletedTasks());
    assertEquals(expected.getFailedTasks(), actual.getFailedTasks());
  }
}
