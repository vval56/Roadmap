package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.model.ItemStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoadMapItemService {

  RoadMapItemDto create(RoadMapItemDto dto);

  RoadMapItemDto getById(Long id);

  List<RoadMapItemDto> getAll();

  RoadMapItemDto update(Long id, RoadMapItemDto dto);

  void delete(Long id);

  List<RoadMapItemWithTagsDto> getAllWithNplusOne();

  List<RoadMapItemWithTagsDto> getAllWithEntityGraph();

  List<RoadMapItemWithTagsDto> getAllWithFetchJoin();

  Page<RoadMapItemDto> searchWithJpql(String ownerEmail, String roadMapTitle,
                                      String parentTitle, String tagName,
                                      ItemStatus status, Pageable pageable);

  Page<RoadMapItemDto> searchWithNative(String ownerEmail, String roadMapTitle,
                                        String parentTitle, String tagName,
                                        ItemStatus status, Pageable pageable);
}
