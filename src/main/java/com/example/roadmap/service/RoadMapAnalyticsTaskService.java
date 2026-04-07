package com.example.roadmap.service;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadMapAnalyticsTaskService {

  private final AsyncTaskRegistryService asyncTaskRegistryService;
  private final RoadMapAnalyticsAsyncWorker roadMapAnalyticsAsyncWorker;

  public AsyncTaskSubmissionDto submitRoadMapReport(Long roadMapId) {
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(roadMapId);
    roadMapAnalyticsAsyncWorker.generateReportAsync(taskId, roadMapId);
    return new AsyncTaskSubmissionDto(taskId, AsyncTaskStatus.PENDING, "/api/async-tasks/" + taskId);
  }

  public AsyncTaskStatusDto getTaskStatus(String taskId) {
    return asyncTaskRegistryService.getStatus(taskId);
  }

  public AsyncTaskCountersDto getCounters() {
    return asyncTaskRegistryService.getCounters();
  }
}
