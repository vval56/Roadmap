package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoadMapItemServiceImpl component.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoadMapItemServiceImpl implements RoadMapItemService {

  private static final String NOT_FOUND_SUFFIX = " not found";

  private final RoadMapItemRepository roadMapItemRepository;
  private final RoadMapRepository roadMapRepository;
  private final TagRepository tagRepository;

  @Override
  public RoadMapItemDto create(RoadMapItemDto dto) {
    RoadMapItem entity = new RoadMapItem();
    RoadMapItemMapper.copyToEntity(dto, entity);
    entity.setRoadMap(getRoadMap(dto.getRoadMapId()));
    entity.setTags(getTags(dto.getTagIds()));
    return RoadMapItemMapper.toDto(roadMapItemRepository.save(entity));
  }

  @Override
  public RoadMapItemDto getById(Long id) {
    return RoadMapItemMapper.toDto(getEntity(id));
  }

  @Override
  public List<RoadMapItemDto> getAll() {
    List<RoadMapItemDto> result = new ArrayList<>();
    for (RoadMapItem item : roadMapItemRepository.findAll()) {
      result.add(RoadMapItemMapper.toDto(item));
    }
    return result;
  }

  @Override
  public RoadMapItemDto update(Long id, RoadMapItemDto dto) {
    RoadMapItem entity = getEntity(id);
    RoadMapItemMapper.copyToEntity(dto, entity);
    entity.setRoadMap(getRoadMap(dto.getRoadMapId()));
    entity.setTags(getTags(dto.getTagIds()));
    return RoadMapItemMapper.toDto(roadMapItemRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    RoadMapItem entity = getEntity(id);
    roadMapItemRepository.delete(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemWithTagsDto> getAllWithNplusOne() {
    List<RoadMapItemWithTagsDto> result = new ArrayList<>();
    for (RoadMapItem item : roadMapItemRepository.findAll()) {
      result.add(RoadMapItemMapper.toWithTagsDto(item));
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemWithTagsDto> getAllWithEntityGraph() {
    List<RoadMapItemWithTagsDto> result = new ArrayList<>();
    for (RoadMapItem item : roadMapItemRepository.findAllWithTagsEntityGraph()) {
      result.add(RoadMapItemMapper.toWithTagsDto(item));
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemWithTagsDto> getAllWithFetchJoin() {
    List<RoadMapItemWithTagsDto> result = new ArrayList<>();
    for (RoadMapItem item : roadMapItemRepository.findAllWithTagsFetchJoin()) {
      result.add(RoadMapItemMapper.toWithTagsDto(item));
    }
    return result;
  }

  private RoadMapItem getEntity(Long id) {
    return roadMapItemRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "RoadMapItem with id=" + id + NOT_FOUND_SUFFIX));
  }

  private RoadMap getRoadMap(Long id) {
    return roadMapRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("RoadMap with id=" + id
            + NOT_FOUND_SUFFIX));
  }

  private Set<Tag> getTags(Set<Long> ids) {
    Set<Tag> tags = new LinkedHashSet<>();
    if (ids == null) {
      return tags;
    }

    for (Long id : ids) {
      Tag tag = tagRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Tag with id=" + id
              + NOT_FOUND_SUFFIX));
      tags.add(tag);
    }
    return tags;
  }
}
