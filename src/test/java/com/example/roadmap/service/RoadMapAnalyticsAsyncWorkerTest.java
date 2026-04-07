package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.RoadMapAnalyticsReportDto;
import com.example.roadmap.model.Comment;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.RoadMapRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadMapAnalyticsAsyncWorkerTest {

  @Mock
  private RoadMapRepository roadMapRepository;

  @Mock
  private AsyncTaskRegistryService asyncTaskRegistryService;

  @InjectMocks
  private RoadMapAnalyticsAsyncWorker roadMapAnalyticsAsyncWorker;

  @Test
  void shouldGenerateAnalyticsReportAndCompleteTask() {
    RoadMap roadMap = roadMap();
    when(roadMapRepository.findDetailedById(2L)).thenReturn(Optional.of(roadMap));

    roadMapAnalyticsAsyncWorker.generateReportAsync("report-1001", 2L).join();

    ArgumentCaptor<RoadMapAnalyticsReportDto> reportCaptor = ArgumentCaptor.forClass(RoadMapAnalyticsReportDto.class);
    verify(asyncTaskRegistryService).markRunning("report-1001");
    verify(asyncTaskRegistryService).complete(eq("report-1001"), reportCaptor.capture());

    RoadMapAnalyticsReportDto report = reportCaptor.getValue();
    assertEquals(2L, report.getRoadMapId());
    assertEquals("Java Backend Roadmap", report.getRoadMapTitle());
    assertEquals("vladislav@example.com", report.getOwnerEmail());
    assertEquals(2, report.getTotalItems());
    assertEquals(1, report.getPlannedItems());
    assertEquals(1, report.getDoneItems());
    assertEquals(2, report.getTotalComments());
    assertEquals(50.0, report.getCompletionRatePercent());
    assertEquals(java.util.List.of("spring", "sql"), report.getDistinctTagNames());
  }

  @Test
  void shouldFailTaskWhenRoadMapDoesNotExist() {
    when(roadMapRepository.findDetailedById(999L)).thenReturn(Optional.empty());

    roadMapAnalyticsAsyncWorker.generateReportAsync("report-1002", 999L).join();

    verify(asyncTaskRegistryService).markRunning("report-1002");
    verify(asyncTaskRegistryService).fail("report-1002", "RoadMap with id=999 not found");
  }

  @Test
  void shouldFailTaskWithGenericMessageWhenRepositoryThrowsBlankMessage() {
    when(roadMapRepository.findDetailedById(2L)).thenThrow(new IllegalStateException("   "));

    roadMapAnalyticsAsyncWorker.generateReportAsync("report-1003", 2L).join();

    verify(asyncTaskRegistryService).markRunning("report-1003");
    verify(asyncTaskRegistryService).fail("report-1003", "Async report generation failed");
  }

  @Test
  void shouldFailTaskWithGenericMessageWhenRepositoryThrowsNullMessage() {
    when(roadMapRepository.findDetailedById(8L)).thenThrow(new IllegalStateException());

    roadMapAnalyticsAsyncWorker.generateReportAsync("report-1006", 8L).join();

    verify(asyncTaskRegistryService).markRunning("report-1006");
    verify(asyncTaskRegistryService).fail("report-1006", "Async report generation failed");
  }

  @Test
  void shouldFailTaskWhenThreadIsInterruptedBeforeSleep() {
    Thread.currentThread().interrupt();
    try {
      roadMapAnalyticsAsyncWorker.generateReportAsync("report-1004", 2L).join();
    } finally {
      Thread.interrupted();
    }

    verify(asyncTaskRegistryService).markRunning("report-1004");
    verify(asyncTaskRegistryService).fail("report-1004", "Async report generation was interrupted");
    verifyNoMoreInteractions(roadMapRepository);
  }

  @Test
  void shouldGenerateZeroCompletionRateForEmptyRoadMap() {
    RoadMap roadMap = new RoadMap();
    User owner = new User();
    owner.setEmail("vladislav@example.com");
    roadMap.setId(7L);
    roadMap.setTitle("Empty roadmap");
    roadMap.setOwner(owner);
    when(roadMapRepository.findDetailedById(7L)).thenReturn(Optional.of(roadMap));

    roadMapAnalyticsAsyncWorker.generateReportAsync("report-1005", 7L).join();

    ArgumentCaptor<RoadMapAnalyticsReportDto> reportCaptor = ArgumentCaptor.forClass(RoadMapAnalyticsReportDto.class);
    verify(asyncTaskRegistryService).complete(eq("report-1005"), reportCaptor.capture());
    RoadMapAnalyticsReportDto report = reportCaptor.getValue();
    assertEquals(0, report.getTotalItems());
    assertEquals(0, report.getPlannedItems());
    assertEquals(0, report.getInProgressItems());
    assertEquals(0, report.getDoneItems());
    assertEquals(0, report.getTotalComments());
    assertEquals(0.0, report.getCompletionRatePercent());
    assertEquals(java.util.List.of(), report.getDistinctTagNames());
  }

  private RoadMap roadMap() {
    User owner = new User();
    owner.setId(1L);
    owner.setEmail("vladislav@example.com");

    Tag spring = new Tag();
    spring.setId(1L);
    spring.setName("spring");

    Tag sql = new Tag();
    sql.setId(2L);
    sql.setName("sql");

    Comment firstComment = new Comment();
    firstComment.setId(10L);
    firstComment.setContent("First");

    Comment secondComment = new Comment();
    secondComment.setId(11L);
    secondComment.setContent("Second");

    RoadMapItem firstItem = new RoadMapItem();
    firstItem.setId(100L);
    firstItem.setTitle("Item 1");
    firstItem.setStatus(ItemStatus.PLANNED);
    firstItem.getTags().add(spring);
    firstItem.getComments().add(firstComment);

    RoadMapItem secondItem = new RoadMapItem();
    secondItem.setId(101L);
    secondItem.setTitle("Item 2");
    secondItem.setStatus(ItemStatus.DONE);
    secondItem.getTags().add(sql);
    secondItem.getComments().add(secondComment);

    RoadMap roadMap = new RoadMap();
    roadMap.setId(2L);
    roadMap.setTitle("Java Backend Roadmap");
    roadMap.setOwner(owner);
    roadMap.getItems().add(firstItem);
    roadMap.getItems().add(secondItem);
    return roadMap;
  }
}
