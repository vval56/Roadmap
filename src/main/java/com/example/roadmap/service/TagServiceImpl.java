package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.TagDto;
import com.example.roadmap.dto.TagMapper;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final RoadMapItemSearchIndexService searchIndexService;

  @Override
  public TagDto create(TagDto dto) {
    Tag entity = new Tag();
    TagMapper.copyToEntity(dto, entity);
    TagDto saved = TagMapper.toDto(tagRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
  }

  @Override
  public TagDto getById(Long id) {
    return TagMapper.toDto(getEntity(id));
  }

  @Override
  public List<TagDto> getAll() {
    List<TagDto> result = new ArrayList<>();
    for (Tag tag : tagRepository.findAll()) {
      result.add(TagMapper.toDto(tag));
    }
    return result;
  }

  @Override
  public TagDto update(Long id, TagDto dto) {
    Tag entity = getEntity(id);
    TagMapper.copyToEntity(dto, entity);
    TagDto saved = TagMapper.toDto(tagRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
  }

  @Override
  public void delete(Long id) {
    if (tagRepository.existsById(id)) {
      tagRepository.deleteById(id);
      searchIndexService.invalidateAll();
    }
  }

  private Tag getEntity(Long id) {
    return tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tag with id=" + id + " not found"));
  }
}
