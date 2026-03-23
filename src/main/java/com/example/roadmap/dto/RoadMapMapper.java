package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMap;

public final class RoadMapMapper {

  private RoadMapMapper() {
  }

    public static RoadMapDto toDto(RoadMap entity) {
    RoadMapDto dto = new RoadMapDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setDescription(entity.getDescription());
    dto.setOwnerId(entity.getOwner().getId());
    return dto;
  }

    public static void copyToEntity(RoadMapDto dto, RoadMap entity) {
    entity.setTitle(dto.getTitle());
    entity.setDescription(dto.getDescription());
  }
}
