package com.example.roadmap.service;

import com.example.roadmap.dto.RoadMapDto;
import java.util.List;

public interface RoadMapService {

  RoadMapDto create(RoadMapDto dto);

  RoadMapDto getById(Long id);

  RoadMapDto getByTitle(String title);

  List<RoadMapDto> getAll();

  RoadMapDto update(Long id, RoadMapDto dto);

  void delete(Long id);
}
