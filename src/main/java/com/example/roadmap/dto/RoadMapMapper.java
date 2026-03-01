package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMap;

/**
 * Mapper for roadmap DTOs.
 */
public final class RoadMapMapper {

  private RoadMapMapper() {
  }

  /**
   * Maps roadmap entity to DTO.
   *
   * @param entity roadmap entity
   * @return mapped DTO
   */
  public static RoadMapDto toDto(RoadMap entity) {
    RoadMapDto dto = new RoadMapDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setDescription(entity.getDescription());
    dto.setOwnerId(entity.getOwner().getId());
    return dto;
  }

  /**
   * Copies DTO fields to entity.
   *
   * @param dto source DTO
   * @param entity target entity
   */
  public static void copyToEntity(RoadMapDto dto, RoadMap entity) {
    entity.setTitle(dto.getTitle());
    entity.setDescription(dto.getDescription());
  }
}
