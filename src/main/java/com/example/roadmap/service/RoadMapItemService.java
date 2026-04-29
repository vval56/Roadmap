package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.model.ItemStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoadMapItemService {

  RoadMapItemDto create(RoadMapItemDto dto);

  List<RoadMapItemDto> createBulk(Long roadMapId, List<RoadMapItemBulkCreateDto> dtos);

  List<RoadMapItemDto> createBulkWithoutTransactional(Long roadMapId, List<RoadMapItemBulkCreateDto> dtos);

  List<RoadMapItemDto> createBulkWithTransactional(Long roadMapId, List<RoadMapItemBulkCreateDto> dtos);

  RoadMapItemDto getById(Long id);

  List<RoadMapItemDto> getByRoadMapId(Long roadMapId);

  List<RoadMapItemDto> getAll();

  Page<RoadMapItemDto> getPage(Pageable pageable);

  RoadMapItemDto update(Long id, RoadMapItemDto dto);

  void delete(Long id);

  List<RoadMapItemWithTagsDto> getAllWithNplusOne();

  List<RoadMapItemWithTagsDto> getAllWithEntityGraph();

  List<RoadMapItemWithTagsDto> getAllWithFetchJoin();

  List<RoadMapItemDto> searchWithJpql(String ownerEmail, String roadMapTitle,
                                      String parentTitle, String tagName,
                                      ItemStatus status);

  List<RoadMapItemDto> searchWithNative(String ownerEmail, String roadMapTitle,
                                        String parentTitle, String tagName,
                                        ItemStatus status);
}
