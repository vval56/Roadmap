package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.AsyncRoadMapItemBulkResultDto;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadMapItemBulkAsyncWorkerTest {

  @Mock
  private RoadMapItemService roadMapItemService;

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @InjectMocks
  private RoadMapItemBulkAsyncWorker roadMapItemBulkAsyncWorker;

  @Test
  void shouldCreateBulkItemsAndCompleteTask() {
    List<RoadMapItemBulkCreateDto> dtos = List.of(
        bulkItem("Bulk async item", ItemStatus.PLANNED),
        bulkItem("Bulk async item", ItemStatus.IN_PROGRESS)
    );
    when(roadMapItemService.createBulk(32L, dtos)).thenReturn(List.of(savedItem(101L), savedItem(102L)));

    roadMapItemBulkAsyncWorker.createBulkAsync("bulk-task-1", 32L, dtos).join();

    ArgumentCaptor<AsyncRoadMapItemBulkResultDto> resultCaptor =
        ArgumentCaptor.forClass(AsyncRoadMapItemBulkResultDto.class);
    verify(asyncTaskRegistryService).markRunning("bulk-task-1");
    verify(asyncTaskRegistryService).completeBulk(eq("bulk-task-1"), resultCaptor.capture());

    AsyncRoadMapItemBulkResultDto result = resultCaptor.getValue();
    assertEquals(32L, result.getRoadMapId());
    assertEquals(2, result.getCreatedItemsCount());
    assertEquals(List.of(101L, 102L), result.getCreatedItemIds());
  }

  @Test
  void shouldFailTaskWhenBulkCreateThrowsException() {
    List<RoadMapItemBulkCreateDto> dtos = List.of(bulkItem("Broken bulk item", ItemStatus.PLANNED));
    when(roadMapItemService.createBulk(77L, dtos))
        .thenThrow(new ResourceNotFoundException("RoadMap with id=77 not found"));

    roadMapItemBulkAsyncWorker.createBulkAsync("bulk-task-2", 77L, dtos).join();

    verify(asyncTaskRegistryService).markRunning("bulk-task-2");
    verify(asyncTaskRegistryService).fail("bulk-task-2", "RoadMap with id=77 not found");
  }

  @Test
  void shouldFailTaskWhenThreadIsInterruptedBeforeDelay() {
    List<RoadMapItemBulkCreateDto> dtos = List.of(bulkItem("Interrupted item", ItemStatus.PLANNED));

    Thread.currentThread().interrupt();
    try {
      roadMapItemBulkAsyncWorker.createBulkAsync("bulk-task-3", 32L, dtos).join();
    } finally {
      Thread.interrupted();
    }

    verify(asyncTaskRegistryService).markRunning("bulk-task-3");
    verify(asyncTaskRegistryService).fail("bulk-task-3", "Async roadmap item bulk creation was interrupted");
  }

  private RoadMapItemBulkCreateDto bulkItem(String title, ItemStatus status) {
    RoadMapItemBulkCreateDto dto = new RoadMapItemBulkCreateDto();
    dto.setTitle(title);
    dto.setDetails("Created asynchronously");
    dto.setStatus(status);
    dto.setTagIds(Set.of());
    return dto;
  }

  private RoadMapItemDto savedItem(Long id) {
    RoadMapItemDto dto = new RoadMapItemDto();
    dto.setId(id);
    dto.setRoadMapId(32L);
    dto.setTitle("Bulk async item");
    dto.setDetails("Created asynchronously");
    dto.setStatus(ItemStatus.PLANNED);
    dto.setTagIds(Set.of());
    return dto;
  }
}
