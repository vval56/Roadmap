package com.example.roadmap.service;

import com.example.roadmap.dto.TagDto;
import java.util.List;

/**
 * Service contract for tags.
 */
public interface TagService {

  TagDto create(TagDto dto);

  TagDto getById(Long id);

  List<TagDto> getAll();

  TagDto update(Long id, TagDto dto);

  void delete(Long id);
}
