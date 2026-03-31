package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.dto.TransactionDemoResultDto;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionDemoServiceImplTest {

  @Mock
  private TransactionWorkerService transactionWorkerService;

  @Mock
  private RoadMapRepository roadMapRepository;

  @Mock
  private RoadMapItemRepository roadMapItemRepository;

  @Mock
  private RoadMapItemSearchIndexService searchIndexService;

  @InjectMocks
  private TransactionDemoServiceImpl transactionDemoService;

  @Test
  void runWithoutTransactionalShouldReturnAfterSnapshotWhenFailureHappens() {
    TransactionDemoRequestDto requestDto = demoRequest();

    when(roadMapRepository.count()).thenReturn(1L, 2L);
    when(roadMapItemRepository.count()).thenReturn(3L, 5L);
    doThrow(new IllegalStateException("Forced bulk failure after saving 2 items"))
        .when(transactionWorkerService).saveWithoutTransactionalAndFail(requestDto);

    TransactionDemoResultDto result = transactionDemoService.runWithoutTransactional(requestDto);

    assertFalse(result.isTransactional());
    assertEquals(2, result.getRequestedItems());
    assertEquals(1L, result.getRoadMapsBefore());
    assertEquals(2L, result.getRoadMapsAfter());
    assertEquals(3L, result.getItemsBefore());
    assertEquals(5L, result.getItemsAfter());
    assertEquals("Forced bulk failure after saving 2 items", result.getMessage());

    verify(transactionWorkerService).saveWithoutTransactionalAndFail(requestDto);
    verify(searchIndexService).invalidateAll();
  }

  @Test
  void runWithTransactionalShouldReturnRollbackSnapshotWhenFailureHappens() {
    TransactionDemoRequestDto requestDto = demoRequest();

    when(roadMapRepository.count()).thenReturn(4L, 4L);
    when(roadMapItemRepository.count()).thenReturn(8L, 8L);
    doThrow(new IllegalStateException("Forced bulk failure after saving 2 items"))
        .when(transactionWorkerService).saveWithTransactionalAndFail(requestDto);

    TransactionDemoResultDto result = transactionDemoService.runWithTransactional(requestDto);

    assertTrue(result.isTransactional());
    assertEquals(2, result.getRequestedItems());
    assertEquals(4L, result.getRoadMapsBefore());
    assertEquals(4L, result.getRoadMapsAfter());
    assertEquals(8L, result.getItemsBefore());
    assertEquals(8L, result.getItemsAfter());
    assertEquals("Forced bulk failure after saving 2 items", result.getMessage());

    verify(transactionWorkerService).saveWithTransactionalAndFail(requestDto);
    verify(searchIndexService).invalidateAll();
  }

  @Test
  void runWithTransactionalShouldReturnSuccessSnapshotWhenNoExceptionHappens() {
    TransactionDemoRequestDto requestDto = demoRequest();

    when(roadMapRepository.count()).thenReturn(7L, 8L);
    when(roadMapItemRepository.count()).thenReturn(11L, 13L);
    doNothing().when(transactionWorkerService).saveWithTransactionalAndFail(requestDto);

    TransactionDemoResultDto result = transactionDemoService.runWithTransactional(requestDto);

    assertTrue(result.isTransactional());
    assertEquals(2, result.getRequestedItems());
    assertEquals(7L, result.getRoadMapsBefore());
    assertEquals(8L, result.getRoadMapsAfter());
    assertEquals(11L, result.getItemsBefore());
    assertEquals(13L, result.getItemsAfter());
    assertEquals("Bulk operation completed without exception", result.getMessage());

    verify(transactionWorkerService).saveWithTransactionalAndFail(requestDto);
    verify(searchIndexService).invalidateAll();
  }

  @Test
  void runWithoutTransactionalShouldReturnSuccessSnapshotWhenNoExceptionHappens() {
    TransactionDemoRequestDto requestDto = demoRequest();

    when(roadMapRepository.count()).thenReturn(10L, 11L);
    when(roadMapItemRepository.count()).thenReturn(20L, 21L);
    doNothing().when(transactionWorkerService).saveWithoutTransactionalAndFail(requestDto);

    TransactionDemoResultDto result = transactionDemoService.runWithoutTransactional(requestDto);

    assertFalse(result.isTransactional());
    assertEquals(2, result.getRequestedItems());
    assertEquals(10L, result.getRoadMapsBefore());
    assertEquals(11L, result.getRoadMapsAfter());
    assertEquals(20L, result.getItemsBefore());
    assertEquals(21L, result.getItemsAfter());
    assertEquals("Bulk operation completed without exception", result.getMessage());

    verify(transactionWorkerService).saveWithoutTransactionalAndFail(requestDto);
    verify(searchIndexService).invalidateAll();
  }

  private TransactionDemoRequestDto demoRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Bulk Tx Demo");
    requestDto.setItems(List.of(
        bulkItem("Item 1", "First", ItemStatus.PLANNED),
        bulkItem("Item 2", "Second", ItemStatus.PLANNED)
    ));
    return requestDto;
  }

  private RoadMapItemBulkCreateDto bulkItem(String title, String details, ItemStatus status) {
    RoadMapItemBulkCreateDto dto = new RoadMapItemBulkCreateDto();
    dto.setTitle(title);
    dto.setDetails(details);
    dto.setStatus(status);
    return dto;
  }
}
