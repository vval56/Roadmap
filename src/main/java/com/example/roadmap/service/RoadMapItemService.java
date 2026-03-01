package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import java.util.List;

/**
 * Service contract for roadmap items.
 */
public interface RoadMapItemService {

  RoadMapItemDto create(RoadMapItemDto dto);

  RoadMapItemDto getById(Long id);

  List<RoadMapItemDto> getAll();

  RoadMapItemDto update(Long id, RoadMapItemDto dto);

  void delete(Long id);

  List<RoadMapItemWithTagsDto> getAllWithNplusOne();

  List<RoadMapItemWithTagsDto> getAllWithEntityGraph();

  List<RoadMapItemWithTagsDto> getAllWithFetchJoin();
}
