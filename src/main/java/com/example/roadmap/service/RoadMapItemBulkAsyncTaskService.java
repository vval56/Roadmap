package com.example.roadmap.service;

import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskSubmissionDto;
import com.example.roadmap.dto.AsyncTaskType;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.repository.RoadMapRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadMapItemBulkAsyncTaskService {

  private final AsyncTaskRegistryService asyncTaskRegistryService;
  private final RoadMapItemBulkAsyncWorker roadMapItemBulkAsyncWorker;
  private final RoadMapRepository roadMapRepository;

  public AsyncTaskSubmissionDto submitBulkCreate(Long roadMapId, List<RoadMapItemBulkCreateDto> dtos) {
    validateRoadMapExists(roadMapId);
    String taskId = asyncTaskRegistryService.registerRoadMapItemBulkTask(roadMapId);
    roadMapItemBulkAsyncWorker.createBulkAsync(taskId, roadMapId, dtos);
    return new AsyncTaskSubmissionDto(
        taskId,
        AsyncTaskType.ROADMAP_ITEM_BULK_CREATE,
        AsyncTaskStatus.PENDING,
        "/api/async-tasks/" + taskId
    );
  }

  private void validateRoadMapExists(Long roadMapId) {
    if (!roadMapRepository.existsById(roadMapId)) {
      throw new ResourceNotFoundException("RoadMap with id=" + roadMapId + " not found");
    }
  }
}
