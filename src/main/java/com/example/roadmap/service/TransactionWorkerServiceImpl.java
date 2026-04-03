package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.TransactionDemoRequestDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import com.example.roadmap.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionWorkerServiceImpl implements TransactionWorkerService {

  private static final String NOT_FOUND_SUFFIX = " not found";

  private final UserRepository userRepository;
  private final RoadMapRepository roadMapRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final TagRepository tagRepository;

  @Override
  public void saveWithoutTransactionalAndFail(TransactionDemoRequestDto requestDto) {
    User owner = getOwner(requestDto.getOwnerId());

    RoadMap roadMap = new RoadMap();
    roadMap.setTitle(requestDto.getRoadMapTitle());
    roadMap.setDescription("Bulk scenario without @Transactional");
    roadMap.setOwner(owner);
    roadMapRepository.save(roadMap);

    saveItemsAndFail(roadMap, requestDto.getItems());
  }

  @Override
  @Transactional
  public void saveWithTransactionalAndFail(TransactionDemoRequestDto requestDto) {
    User owner = getOwner(requestDto.getOwnerId());

    RoadMap roadMap = new RoadMap();
    roadMap.setTitle(requestDto.getRoadMapTitle());
    roadMap.setDescription("Bulk scenario with @Transactional");
    roadMap.setOwner(owner);
    roadMapRepository.save(roadMap);

    saveItemsAndFail(roadMap, requestDto.getItems());
  }

  private User getOwner(Long ownerId) {
    return userRepository.findById(ownerId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "User with id=" + ownerId + NOT_FOUND_SUFFIX));
  }

  private void saveItemsAndFail(RoadMap roadMap, List<RoadMapItemBulkCreateDto> itemDtos) {
    for (RoadMapItemBulkCreateDto itemDto : itemDtos) {
      roadMapItemRepository.save(toEntity(itemDto, roadMap));
    }
  }

  private RoadMapItem toEntity(RoadMapItemBulkCreateDto dto, RoadMap roadMap) {
    RoadMapItem entity = new RoadMapItem();
    entity.setTitle(dto.getTitle());
    entity.setDetails(normalizeDetails(dto.getDetails()));
    entity.setStatus(dto.getStatus());
    entity.setRoadMap(roadMap);
    entity.setParentItem(getParentItem(dto.getParentItemId()));
    entity.setTags(getTags(dto.getTagIds()));
    return entity;
  }

  private RoadMapItem getParentItem(Long parentItemId) {
    return Optional.ofNullable(parentItemId)
        .map(this::getExistingItem)
        .orElse(null);
  }

  private RoadMapItem getExistingItem(Long itemId) {
    return roadMapItemRepository.findById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "RoadMapItem with id=" + itemId + NOT_FOUND_SUFFIX));
  }

  private Set<Tag> getTags(Set<Long> tagIds) {
    return Optional.ofNullable(tagIds)
        .orElseGet(Set::of)
        .stream()
        .map(this::getTag)
        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
  }

  private Tag getTag(Long tagId) {
    return tagRepository.findById(tagId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Tag with id=" + tagId + NOT_FOUND_SUFFIX));
  }

  private String normalizeDetails(String details) {
    return Optional.ofNullable(details)
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .orElse(null);
  }
}
