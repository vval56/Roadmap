package com.example.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import com.example.roadmap.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionWorkerServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoadMapRepository roadMapRepository;

  @Mock
  private RoadMapItemRepository roadMapItemRepository;

  @Mock
  private TagRepository tagRepository;

  @InjectMocks
  private TransactionWorkerServiceImpl transactionWorkerService;

  @Test
  void saveWithoutTransactionalAndFailShouldKeepFirstItemWhenSecondItemIsInvalid() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = missingTagOnSecondItemRequest();

    Tag springTag = new Tag();
    springTag.setId(2L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(tagRepository.findById(2L)).thenReturn(Optional.of(springTag));
    when(tagRepository.findById(999999L)).thenReturn(Optional.empty());
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> {
      RoadMap roadMap = invocation.getArgument(0);
      roadMap.setId(10L);
      return roadMap;
    });
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> transactionWorkerService.saveWithoutTransactionalAndFail(requestDto));

    assertEquals("Tag with id=999999 not found", exception.getMessage());
    verify(roadMapRepository).save(any(RoadMap.class));
    verify(roadMapItemRepository, times(1)).save(any(RoadMapItem.class));

    ArgumentCaptor<RoadMap> roadMapCaptor = ArgumentCaptor.forClass(RoadMap.class);
    verify(roadMapRepository).save(roadMapCaptor.capture());
    assertEquals("Bulk scenario without @Transactional", roadMapCaptor.getValue().getDescription());
  }

  @Test
  void saveWithTransactionalAndFailShouldThrowWhenOwnerDoesNotExist() {
    TransactionDemoRequestDto requestDto = missingOwnerRequest();

    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> transactionWorkerService.saveWithTransactionalAndFail(requestDto));

    assertEquals("User with id=99 not found", exception.getMessage());
  }

  @Test
  void saveWithTransactionalAndFailShouldUseParentTagAndFailOnBrokenSecondItem() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = transactionalFailureRequest();

    Tag springTag = new Tag();
    springTag.setId(2L);

    RoadMapItem parentItem = new RoadMapItem();
    parentItem.setId(5L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(tagRepository.findById(2L)).thenReturn(Optional.of(springTag));
    when(tagRepository.findById(999999L)).thenReturn(Optional.empty());
    when(roadMapItemRepository.findById(5L)).thenReturn(Optional.of(parentItem));
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> transactionWorkerService.saveWithTransactionalAndFail(requestDto));

    assertEquals("Tag with id=999999 not found", exception.getMessage());

    ArgumentCaptor<RoadMap> roadMapCaptor = ArgumentCaptor.forClass(RoadMap.class);
    verify(roadMapRepository).save(roadMapCaptor.capture());
    assertEquals("Bulk scenario with @Transactional", roadMapCaptor.getValue().getDescription());

    ArgumentCaptor<RoadMapItem> itemCaptor = ArgumentCaptor.forClass(RoadMapItem.class);
    verify(roadMapItemRepository, times(1)).save(itemCaptor.capture());
    assertNull(itemCaptor.getAllValues().getFirst().getDetails());
    assertEquals(parentItem, itemCaptor.getAllValues().getFirst().getParentItem());
    assertEquals(Set.of(springTag), itemCaptor.getAllValues().getFirst().getTags());
  }

  @Test
  void saveWithoutTransactionalAndFailShouldThrowWhenTagDoesNotExist() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = missingTagRequest();

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(tagRepository.findById(77L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> transactionWorkerService.saveWithoutTransactionalAndFail(requestDto));

    assertEquals("Tag with id=77 not found", exception.getMessage());
    verify(roadMapItemRepository, times(1)).save(any(RoadMapItem.class));
  }

  @Test
  void saveWithoutTransactionalShouldCompleteWhenOnlyOneItemIsProvided() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = singleItemRequest();

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertDoesNotThrow(() -> transactionWorkerService.saveWithoutTransactionalAndFail(requestDto));

    verify(roadMapItemRepository, times(1)).save(any(RoadMapItem.class));
  }

  @Test
  void saveWithTransactionalShouldCompleteWhenOnlyOneItemIsProvided() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = singleItemRequest();

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(roadMapItemRepository.save(any(RoadMapItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertDoesNotThrow(() -> transactionWorkerService.saveWithTransactionalAndFail(requestDto));

    verify(roadMapItemRepository, times(1)).save(any(RoadMapItem.class));
  }

  @Test
  void saveWithTransactionalAndFailShouldThrowWhenParentDoesNotExist() {
    User owner = new User();
    owner.setId(1L);
    TransactionDemoRequestDto requestDto = missingParentRequest();

    when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
    when(roadMapRepository.save(any(RoadMap.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(roadMapItemRepository.findById(55L)).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
        () -> transactionWorkerService.saveWithTransactionalAndFail(requestDto));

    assertEquals("RoadMapItem with id=55 not found", exception.getMessage());
  }

  private TransactionDemoRequestDto missingTagOnSecondItemRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Broken second item");
    requestDto.setItems(List.of(
        bulkItem("Step 1", " First ", ItemStatus.PLANNED, Set.of(2L)),
        bulkItem("Broken step", " Second ", ItemStatus.IN_PROGRESS, Set.of(999999L))
    ));
    return requestDto;
  }

  private TransactionDemoRequestDto missingOwnerRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(99L);
    requestDto.setRoadMapTitle("Missing owner");
    requestDto.setItems(List.of(
        bulkItem("Step 1", "One", ItemStatus.PLANNED, Set.of()),
        bulkItem("Step 2", "Two", ItemStatus.PLANNED, Set.of())
    ));
    return requestDto;
  }

  private TransactionDemoRequestDto transactionalFailureRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Transactional path");
    requestDto.setItems(List.of(
        bulkItemWithParent("Child step", "   ", ItemStatus.PLANNED, Set.of(2L), 5L),
        bulkItem("Broken step", "Second", ItemStatus.DONE, Set.of(999999L))
    ));
    return requestDto;
  }

  private TransactionDemoRequestDto missingTagRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Missing tag");
    requestDto.setItems(List.of(
        bulkItem("Step 1", "One", ItemStatus.PLANNED, Set.of()),
        bulkItem("Step 2", "Two", ItemStatus.PLANNED, Set.of(77L))
    ));
    return requestDto;
  }

  private TransactionDemoRequestDto singleItemRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Single item");
    requestDto.setItems(List.of(
        bulkItem("Only step", null, ItemStatus.PLANNED, Set.of())
    ));
    return requestDto;
  }

  private TransactionDemoRequestDto missingParentRequest() {
    TransactionDemoRequestDto requestDto = new TransactionDemoRequestDto();
    requestDto.setOwnerId(1L);
    requestDto.setRoadMapTitle("Missing parent");
    requestDto.setItems(List.of(
        bulkItemWithParent("Child step", "One", ItemStatus.PLANNED, Set.of(), 55L),
        bulkItem("Second step", "Two", ItemStatus.PLANNED, Set.of())
    ));
    return requestDto;
  }

  private RoadMapItemBulkCreateDto bulkItem(String title, String details,
                                            ItemStatus status, Set<Long> tagIds) {
    return bulkItemWithParent(title, details, status, tagIds, null);
  }

  private RoadMapItemBulkCreateDto bulkItemWithParent(String title, String details,
                                                      ItemStatus status, Set<Long> tagIds,
                                                      Long parentItemId) {
    RoadMapItemBulkCreateDto dto = new RoadMapItemBulkCreateDto();
    dto.setTitle(title);
    dto.setDetails(details);
    dto.setStatus(status);
    dto.setTagIds(tagIds);
    dto.setParentItemId(parentItemId);
    return dto;
  }
}
