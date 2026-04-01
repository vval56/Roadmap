package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.cache.RoadMapItemSearchKey;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.exception.BusinessRuleViolationException;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.TagRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
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
    entity.setDetails(normalizeDetails(dto.getDetails()));
    entity.setRoadMap(getRoadMap(dto.getRoadMapId()));
    entity.setParentItem(getParentItem(dto.getParentItemId(), null));
    entity.setTags(getTags(dto.getTagIds()));
    RoadMapItemDto saved = RoadMapItemMapper.toDto(roadMapItemRepository.save(entity));
    invalidateSearchIndex();
    return saved;
  }

  @Override
  public List<RoadMapItemDto> createBulk(Long roadMapId, List<RoadMapItemBulkCreateDto> dtos) {
    RoadMap roadMap = getRoadMap(roadMapId);
    List<RoadMapItem> entities = dtos.stream()
        .map(dto -> toBulkEntity(dto, roadMap))
        .toList();
    List<RoadMapItemDto> saved = roadMapItemRepository.saveAll(entities).stream()
        .map(RoadMapItemMapper::toDto)
        .toList();
    invalidateSearchIndex();
    return saved;
  }

  @Override
  public RoadMapItemDto getById(Long id) {
    return RoadMapItemMapper.toDto(getEntity(id));
  }

  @Override
  public List<RoadMapItemDto> getAll() {
    return roadMapItemRepository.findAll().stream()
        .map(RoadMapItemMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<RoadMapItemDto> getPage(Pageable pageable) {
    return roadMapItemRepository.findAll(normalizePageable(pageable)).map(RoadMapItemMapper::toDto);
  }

  @Override
  public RoadMapItemDto update(Long id, RoadMapItemDto dto) {
    RoadMapItem entity = getEntity(id);
    validateStatusTransition(entity, dto);
    RoadMapItemMapper.copyToEntity(dto, entity);
    entity.setDetails(normalizeDetails(dto.getDetails()));
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
    return roadMapItemRepository.findAll().stream()
        .map(RoadMapItemMapper::toWithTagsDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemWithTagsDto> getAllWithEntityGraph() {
    return roadMapItemRepository.findAllWithTagsEntityGraph().stream()
        .map(RoadMapItemMapper::toWithTagsDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<RoadMapItemWithTagsDto> getAllWithFetchJoin() {
    return roadMapItemRepository.findAllWithTagsFetchJoin().stream()
        .map(RoadMapItemMapper::toWithTagsDto)
        .toList();
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
    return roadMapRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "RoadMap with id=" + id + NOT_FOUND_SUFFIX));
  }

  private Set<Tag> getTags(Set<Long> ids) {
    return Optional.ofNullable(ids)
        .orElseGet(Set::of)
        .stream()
        .map(this::getTag)
        .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
  }

  private RoadMapItem getParentItem(Long parentItemId, Long currentItemId) {
    return Optional.ofNullable(parentItemId)
        .filter(id -> currentItemId == null || !currentItemId.equals(id))
        .map(this::getEntity)
        .orElse(null);
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
        RoadMapItemSearchKey.criteria(
            queryType,
            ownerEmail,
            roadMapTitle,
            parentTitle,
            tagName,
            status == null ? null : status.name()
        ),
        RoadMapItemSearchKey.pageDescriptor(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort().toString()
        )
    );
  }

  private String normalizeText(String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .filter(text -> !text.isBlank())
        .orElse(null);
  }

  private String normalizeJpqlText(String value) {
    return Optional.ofNullable(value)
        .map(String::trim)
        .filter(text -> !text.isBlank())
        .orElse("");
  }

  private String normalizeDetails(String value) {
    return normalizeText(value);
  }

  private Tag getTag(Long id) {
    return tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tag with id=" + id + NOT_FOUND_SUFFIX));
  }

  private void validateStatusTransition(RoadMapItem entity, RoadMapItemDto dto) {
    if (dto.getStatus().ordinal() < entity.getStatus().ordinal()) {
      throw new BusinessRuleViolationException(
          "RoadMapItem status cannot move backward from %s to %s"
              .formatted(entity.getStatus(), dto.getStatus()));
    }
  }

  private RoadMapItem toBulkEntity(RoadMapItemBulkCreateDto dto, RoadMap roadMap) {
    RoadMapItem entity = new RoadMapItem();
    entity.setTitle(dto.getTitle());
    entity.setDetails(normalizeDetails(dto.getDetails()));
    entity.setStatus(dto.getStatus());
    entity.setRoadMap(roadMap);
    entity.setParentItem(getParentItem(dto.getParentItemId(), null));
    entity.setTags(getTags(dto.getTagIds()));
    return entity;
  }

  private void invalidateSearchIndex() {
    searchIndexService.invalidateAll();
  }
}
