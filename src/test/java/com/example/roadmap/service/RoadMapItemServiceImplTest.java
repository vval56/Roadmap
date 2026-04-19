package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.exception.BusinessRuleViolationException;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoadMapItemServiceImplTest {

  @Mock
  private RoadMapItemRepository roadMapItemRepository;

  @Mock
  private RoadMapRepository roadMapRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private RoadMapItemSearchIndexService searchIndexService;

  @InjectMocks
  private RoadMapItemServiceImpl roadMapItemService;

  @Test
  void createBulkShouldSaveAllItemsAndInvalidateSearchIndex() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(1L);

    Tag springTag = new Tag();
    springTag.setId(2L);
    springTag.setName("spring");

    when(roadMapRepository.findById(1L)).thenReturn(java.util.Optional.of(roadMap));
    when(tagRepository.findById(2L)).thenReturn(java.util.Optional.of(springTag));

    AtomicReference<List<RoadMapItem>> capturedItems = new AtomicReference<>();
    when(roadMapItemRepository.saveAll(anyList())).thenAnswer(invocation -> {
      List<RoadMapItem> items = invocation.getArgument(0);
      capturedItems.set(items);
      long nextId = 100L;
      for (RoadMapItem item : items) {
        item.setId(nextId++);
      }
      return items;
    });

    List<RoadMapItemDto> result = roadMapItemService.createBulk(1L, List.of(
        bulkItem("Configure Docker", "  Prepare postgres container  ", ItemStatus.PLANNED, Set.of(2L)),
        bulkItem("Write repositories", "   ", ItemStatus.IN_PROGRESS, Set.of())
    ));

    assertEquals(2, result.size());
    assertEquals(100L, result.getFirst().getId());
    assertEquals("Prepare postgres container", result.getFirst().getDetails());
    assertNull(result.get(1).getDetails());
    assertEquals(Set.of(2L), result.getFirst().getTagIds());

    assertEquals(2, capturedItems.get().size());
    assertSame(roadMap, capturedItems.get().getFirst().getRoadMap());
    assertEquals("Prepare postgres container", capturedItems.get().getFirst().getDetails());
    assertNull(capturedItems.get().get(1).getDetails());

    verify(searchIndexService).invalidateAll();
  }

  @Test
  void createBulkShouldThrowWhenTagDoesNotExist() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(1L);
    List<RoadMapItemBulkCreateDto> payload = List.of(
        bulkItem("Broken item", "details", ItemStatus.PLANNED, Set.of(99L))
    );

    when(roadMapRepository.findById(1L)).thenReturn(java.util.Optional.of(roadMap));
    when(tagRepository.findById(99L)).thenReturn(java.util.Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> roadMapItemService.createBulk(1L, payload));

    assertEquals("Tag with id=99 not found", exception.getMessage());
    verify(roadMapItemRepository, never()).saveAll(anyList());
    verify(searchIndexService, never()).invalidateAll();
  }

  @Test
  void updateShouldRejectBackwardStatusTransition() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(2L);

    RoadMapItem existingItem = new RoadMapItem();
    existingItem.setId(37L);
    existingItem.setStatus(ItemStatus.IN_PROGRESS);
    existingItem.setRoadMap(roadMap);

    RoadMapItemDto payload = new RoadMapItemDto();
    payload.setTitle("Learn JPA basics");
    payload.setDetails("Entity mapping, repositories, relationships");
    payload.setStatus(ItemStatus.PLANNED);
    payload.setRoadMapId(2L);
    payload.setTagIds(Set.of());

    when(roadMapItemRepository.findById(37L)).thenReturn(java.util.Optional.of(existingItem));

    BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
        () -> roadMapItemService.update(37L, payload));

    assertEquals("RoadMapItem status cannot move backward from IN_PROGRESS to PLANNED",
        exception.getMessage());
    verify(roadMapItemRepository, never()).save(existingItem);
    verify(searchIndexService, never()).invalidateAll();
  }

  @Test
  void createBulkWithoutTransactionalShouldKeepFirstSavedItemWhenSecondFails() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(1L);

    Tag springTag = new Tag();
    springTag.setId(2L);

    when(roadMapRepository.findById(1L)).thenReturn(java.util.Optional.of(roadMap));
    when(tagRepository.findById(2L)).thenReturn(java.util.Optional.of(springTag));
    when(tagRepository.findById(999999L)).thenReturn(java.util.Optional.empty());
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> {
      RoadMapItem item = invocation.getArgument(0);
      item.setId(101L);
      return item;
    });

    List<RoadMapItemBulkCreateDto> payload = List.of(
        bulkItem("Valid item", "First", ItemStatus.PLANNED, Set.of(2L)),
        bulkItem("Broken item", "Second", ItemStatus.IN_PROGRESS, Set.of(999999L))
    );

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> roadMapItemService.createBulkWithoutTransactional(1L, payload));

    assertEquals("Tag with id=999999 not found", exception.getMessage());
    verify(roadMapItemRepository).save(any(RoadMapItem.class));
    verify(searchIndexService).invalidateAll();
  }

  @Test
  void createBulkWithoutTransactionalShouldReturnDtoForValidPayload() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(1L);

    Tag springTag = new Tag();
    springTag.setId(2L);

    when(roadMapRepository.findById(1L)).thenReturn(java.util.Optional.of(roadMap));
    when(tagRepository.findById(2L)).thenReturn(java.util.Optional.of(springTag));
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> {
      RoadMapItem incoming = invocation.getArgument(0);
      RoadMapItem persisted = new RoadMapItem();
      persisted.setId(101L);
      persisted.setTitle(incoming.getTitle());
      persisted.setDetails(incoming.getDetails());
      persisted.setStatus(incoming.getStatus());
      return persisted;
    });

    List<RoadMapItemBulkCreateDto> payload = List.of(
        bulkItem("Valid item", "First", ItemStatus.PLANNED, Set.of(2L))
    );

    List<RoadMapItemDto> result = roadMapItemService.createBulkWithoutTransactional(1L, payload);

    assertEquals(1, result.size());
    assertEquals(101L, result.getFirst().getId());
    assertEquals(1L, result.getFirst().getRoadMapId());
    assertEquals(Set.of(2L), result.getFirst().getTagIds());
    verify(searchIndexService).invalidateAll();
  }

  @Test
  void createBulkWithTransactionalShouldThrowWhenSecondItemFails() {
    RoadMap roadMap = new RoadMap();
    roadMap.setId(1L);

    Tag springTag = new Tag();
    springTag.setId(2L);

    when(roadMapRepository.findById(1L)).thenReturn(java.util.Optional.of(roadMap));
    when(tagRepository.findById(2L)).thenReturn(java.util.Optional.of(springTag));
    when(tagRepository.findById(999999L)).thenReturn(java.util.Optional.empty());
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    List<RoadMapItemBulkCreateDto> payload = List.of(
        bulkItem("Valid item", "First", ItemStatus.PLANNED, Set.of(2L)),
        bulkItem("Broken item", "Second", ItemStatus.IN_PROGRESS, Set.of(999999L))
    );

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> roadMapItemService.createBulkWithTransactional(1L, payload));

    assertEquals("Tag with id=999999 not found", exception.getMessage());
    verify(roadMapItemRepository).save(any(RoadMapItem.class));
    verify(searchIndexService).invalidateAll();
  }

  private RoadMapItemBulkCreateDto bulkItem(String title, String details,
                                            ItemStatus status, Set<Long> tagIds) {
    RoadMapItemBulkCreateDto dto = new RoadMapItemBulkCreateDto();
    dto.setTitle(title);
    dto.setDetails(details);
    dto.setStatus(status);
    dto.setTagIds(tagIds);
    return dto;
  }
}
