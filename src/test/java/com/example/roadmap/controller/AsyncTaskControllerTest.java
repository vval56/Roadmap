package com.example.roadmap.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import com.example.roadmap.exception.GlobalExceptionHandler;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.service.RoadMapAnalyticsTaskService;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AsyncTaskController.class)
@Import(GlobalExceptionHandler.class)
class AsyncTaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RoadMapAnalyticsTaskService roadMapAnalyticsTaskService;

  @Test
  void startShouldReturnAcceptedTaskSubmission() throws Exception {
    when(roadMapAnalyticsTaskService.submitRoadMapReport(2L))
        .thenReturn(new AsyncTaskSubmissionDto(
            "report-1001",
            AsyncTaskStatus.PENDING,
            "/api/async-tasks/report-1001"));

    mockMvc.perform(post("/api/async-tasks/roadmaps/2/analytics-report"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.taskId").value("report-1001"))
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.statusEndpoint").value("/api/async-tasks/report-1001"));
  }

  @Test
  void getStatusShouldReturnTaskDetails() throws Exception {
    when(roadMapAnalyticsTaskService.getTaskStatus("report-1001"))
        .thenReturn(new AsyncTaskStatusDto(
            "report-1001",
            2L,
            AsyncTaskStatus.COMPLETED,
            OffsetDateTime.parse("2026-04-07T12:00:00+03:00"),
            OffsetDateTime.parse("2026-04-07T12:00:01+03:00"),
            OffsetDateTime.parse("2026-04-07T12:00:02+03:00"),
            null,
            null));

    mockMvc.perform(get("/api/async-tasks/report-1001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taskId").value("report-1001"))
        .andExpect(jsonPath("$.roadMapId").value(2))
        .andExpect(jsonPath("$.status").value("COMPLETED"));
  }

  @Test
  void getStatusShouldReturnNotFoundForUnknownTask() throws Exception {
    when(roadMapAnalyticsTaskService.getTaskStatus("report-9999"))
        .thenThrow(new ResourceNotFoundException("Async task with id=report-9999 not found"));

    mockMvc.perform(get("/api/async-tasks/report-9999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Async task with id=report-9999 not found"))
        .andExpect(jsonPath("$.path").value("/api/async-tasks/report-9999"));
  }

  @Test
  void getMetricsShouldReturnThreadSafeCounters() throws Exception {
    when(roadMapAnalyticsTaskService.getCounters())
        .thenReturn(new AsyncTaskCountersDto(5, 1, 3, 1));

    mockMvc.perform(get("/api/async-tasks/metrics"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.submittedTasks").value(5))
        .andExpect(jsonPath("$.runningTasks").value(1))
        .andExpect(jsonPath("$.completedTasks").value(3))
        .andExpect(jsonPath("$.failedTasks").value(1));
  }
}
