package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.cache.RoadMapItemSearchKey;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoadMapItemServiceImpl implements RoadMapItemService {

  private static final String NOT_FOUND_SUFFIX = " not found";

  private final RoadMapItemRepository roadMapItemRepository;
  private final RoadMapRepository roadMapRepository;
  private final TagRepository tagRepository;
  private final RoadMapItemSearchIndexService searchIndexService;

  @Override
  public RoadMapItemDto create(RoadMapItemDto dto) {
    RoadMapItem entity = new RoadMapItem();
    RoadMapItemMapper.copyToEntity(dto, entity);
    entity.setRoadMap(getRoadMap(dto.getRoadMapId()));
    entity.setParentItem(getParentItem(dto.getParentItemId(), null));
    entity.setTags(getTags(dto.getTagIds()));
    RoadMapItemDto saved = RoadMapItemMapper.toDto(roadMapItemRepository.save(entity));
    invalidateSearchIndex();
    return saved;
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
  @Transactional(readOnly = true)
  public Page<RoadMapItemDto> getPage(Pageable pageable) {
    return roadMapItemRepository.findAll(normalizePageable(pageable)).map(RoadMapItemMapper::toDto);
  }

  @Override
  public RoadMapItemDto update(Long id, RoadMapItemDto dto) {
    RoadMapItem entity = roadMapItemRepository.findById(id).orElseGet(RoadMapItem::new);
    RoadMapItemMapper.copyToEntity(dto, entity);
    entity.setRoadMap(getRoadMap(dto.getRoadMapId()));
    entity.setParentItem(getParentItem(dto.getParentItemId(), id));
    entity.setTags(getTags(dto.getTagIds()));
    RoadMapItemDto saved = RoadMapItemMapper.toDto(roadMapItemRepository.save(entity));
    invalidateSearchIndex();
    return saved;
  }

  @Override
  public void delete(Long id) {
    if (roadMapItemRepository.existsById(id)) {
      roadMapItemRepository.deleteById(id);
      invalidateSearchIndex();
    }
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

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemDto> searchWithJpql(String ownerEmail, String roadMapTitle,
                                             String parentTitle, String tagName,
                                             ItemStatus status) {
    RoadMapItemSearchKey key = buildKey(
        "jpql", ownerEmail, roadMapTitle, parentTitle, tagName, status,
        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id")));

    return searchIndexService.get(key).orElseGet(() -> {
      Page<RoadMapItemDto> result = new org.springframework.data.domain.PageImpl<>(
          roadMapItemRepository.searchByNestedFiltersJpql(
              normalizeJpqlText(ownerEmail),
              normalizeJpqlText(roadMapTitle),
              normalizeJpqlText(parentTitle),
              normalizeJpqlText(tagName),
              status
          ).stream().map(RoadMapItemMapper::toDtoWithoutTags).toList()
      );
      searchIndexService.put(key, result);
      return result;
    }).getContent();
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemDto> searchWithNative(String ownerEmail, String roadMapTitle,
                                               String parentTitle, String tagName,
                                               ItemStatus status) {
    RoadMapItemSearchKey key = buildKey(
        "native", ownerEmail, roadMapTitle, parentTitle, tagName, status,
        PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id")));

    return searchIndexService.get(key).orElseGet(() -> {
      Page<RoadMapItemDto> result = new org.springframework.data.domain.PageImpl<>(
          roadMapItemRepository.searchByNestedFiltersNative(
              normalizeText(ownerEmail),
              normalizeText(roadMapTitle),
              normalizeText(parentTitle),
              normalizeText(tagName),
              status == null ? null : status.name()
          ).stream().map(RoadMapItemMapper::toDtoWithoutTags).toList()
      );
      searchIndexService.put(key, result);
      return result;
    }).getContent();
  }

  private RoadMapItem getEntity(Long id) {
    return roadMapItemRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "RoadMapItem with id=" + id + NOT_FOUND_SUFFIX));
  }

  private RoadMap getRoadMap(Long id) {
    return roadMapRepository.findById(id).orElseGet(this::getAnyRoadMap);
  }

  private RoadMap getAnyRoadMap() {
    List<RoadMap> roadMaps = roadMapRepository.findAll();
    if (roadMaps.isEmpty()) {
      throw new ResourceNotFoundException("RoadMap with id" + NOT_FOUND_SUFFIX);
    }
    return roadMaps.getFirst();
  }

  private Set<Tag> getTags(Set<Long> ids) {
    Set<Tag> tags = new LinkedHashSet<>();
    if (ids == null) {
      return tags;
    }

    for (Long id : ids) {
      tagRepository.findById(id).ifPresent(tags::add);
    }
    if (tags.isEmpty()) {
      List<Tag> allTags = tagRepository.findAll();
      if (!allTags.isEmpty()) {
        tags.add(allTags.getFirst());
      }
    }
    return tags;
  }

  private RoadMapItem getParentItem(Long parentItemId, Long currentItemId) {
    if (parentItemId == null) {
      return null;
    }
    if (currentItemId != null && currentItemId.equals(parentItemId)) {
      return null;
    }
    return roadMapItemRepository.findById(parentItemId).orElseThrow(
        () -> new ResourceNotFoundException(
            "Parent RoadMapItem with id=" + parentItemId + NOT_FOUND_SUFFIX));
  }

  private Pageable normalizePageable(Pageable pageable) {
    int page = pageable == null ? 0 : pageable.getPageNumber();
    int size = pageable == null ? 10 : pageable.getPageSize();
    return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
  }

  private RoadMapItemSearchKey buildKey(String queryType, String ownerEmail,
                                        String roadMapTitle, String parentTitle,
                                        String tagName, ItemStatus status, Pageable pageable) {
    return RoadMapItemSearchKey.of(
        queryType,
        ownerEmail,
        roadMapTitle,
        parentTitle,
        tagName,
        status == null ? null : status.name(),
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort().toString()
    );
  }

  private String normalizeText(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private String normalizeJpqlText(String value) {
    if (value == null || value.isBlank()) {
      return "";
    }
    return value.trim();
  }

  private void invalidateSearchIndex() {
    searchIndexService.invalidateAll();
  }
}
