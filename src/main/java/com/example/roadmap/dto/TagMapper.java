package com.example.roadmap.dto;

import com.example.roadmap.model.Tag;

/**
 * Mapper for tag DTOs.
 */
public final class TagMapper {

  private TagMapper() {
  }

  /**
   * Maps tag entity to DTO.
   *
   * @param entity tag entity
   * @return mapped DTO
   */
  public static TagDto toDto(Tag entity) {
    TagDto dto = new TagDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    return dto;
  }

  /**
   * Copies DTO fields to entity.
   *
   * @param dto source DTO
   * @param entity target entity
   */
  public static void copyToEntity(TagDto dto, Tag entity) {
    entity.setName(dto.getName());
  }
}
