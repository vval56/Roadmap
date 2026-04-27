package com.example.roadmap.service;

import com.example.roadmap.dto.AsyncTaskCountersDto;
import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskStatusDto;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import com.example.roadmap.dto.AsyncTaskType;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.repository.RoadMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadMapAnalyticsTaskService {

  private final AsyncTaskRegistryService asyncTaskRegistryService;
  private final RoadMapAnalyticsAsyncWorker roadMapAnalyticsAsyncWorker;
  private final RoadMapRepository roadMapRepository;

  public AsyncTaskSubmissionDto submitRoadMapReport(Long roadMapId) {
    validateRoadMapExists(roadMapId);
    String taskId = asyncTaskRegistryService.registerRoadMapReportTask(roadMapId);
    roadMapAnalyticsAsyncWorker.generateReportAsync(taskId, roadMapId);
    return new AsyncTaskSubmissionDto(
        taskId,
        AsyncTaskType.ROADMAP_ANALYTICS_REPORT,
        AsyncTaskStatus.PENDING,
        "/api/async-tasks/" + taskId
    );
  }

  public AsyncTaskStatusDto getTaskStatus(String taskId) {
    return asyncTaskRegistryService.getStatus(taskId);
  }

  public AsyncTaskCountersDto getCounters() {
    return asyncTaskRegistryService.getCounters();
  }

  private void validateRoadMapExists(Long roadMapId) {
    if (!roadMapRepository.existsById(roadMapId)) {
      throw new ResourceNotFoundException("RoadMap with id=" + roadMapId + " not found");
    }
  }
}
