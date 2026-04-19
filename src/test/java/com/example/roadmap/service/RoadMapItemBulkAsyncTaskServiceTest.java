package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.AsyncTaskStatus;
import com.example.roadmap.dto.AsyncTaskType;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.model.ItemStatus;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadMapItemBulkAsyncTaskServiceTest {

  private static final String TASK_ID = "76f99f80-38af-47e9-a38c-50191511c4fe";

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @Mock
  private RoadMapItemBulkAsyncWorker roadMapItemBulkAsyncWorker;

  @InjectMocks
  private RoadMapItemBulkAsyncTaskService roadMapItemBulkAsyncTaskService;

  @Test
  void submitBulkCreateShouldRegisterTaskAndStartAsyncWorker() {
    List<RoadMapItemBulkCreateDto> dtos = List.of(bulkItem("Bulk async item", ItemStatus.PLANNED));
    when(asyncTaskRegistryService.registerRoadMapItemBulkTask(32L)).thenReturn(TASK_ID);
    when(roadMapItemBulkAsyncWorker.createBulkAsync(TASK_ID, 32L, dtos))
        .thenReturn(CompletableFuture.completedFuture(null));

    var response = roadMapItemBulkAsyncTaskService.submitBulkCreate(32L, dtos);

    assertEquals(TASK_ID, response.getTaskId());
    assertEquals(AsyncTaskType.ROADMAP_ITEM_BULK_CREATE, response.getTaskType());
    assertEquals(AsyncTaskStatus.PENDING, response.getStatus());
    assertEquals("/api/async-tasks/" + TASK_ID, response.getStatusEndpoint());
    verify(asyncTaskRegistryService).registerRoadMapItemBulkTask(32L);
    verify(roadMapItemBulkAsyncWorker).createBulkAsync(TASK_ID, 32L, dtos);
  }

  private RoadMapItemBulkCreateDto bulkItem(String title, ItemStatus status) {
    RoadMapItemBulkCreateDto dto = new RoadMapItemBulkCreateDto();
    dto.setTitle(title);
    dto.setDetails("Created asynchronously");
    dto.setStatus(status);
    dto.setTagIds(Set.of());
    return dto;
  }
}
