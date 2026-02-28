package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMapItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts RoadMapItem model objects to DTO objects and back.
 */
public final class RoadMapItemMapper {

  private RoadMapItemMapper() {
  }

  /**
   * Converts model to DTO.
   *
   * @param item model entity
   * @return DTO representation
   */
  public static RoadMapItemDto toDto(RoadMapItem item) {
    if (item == null) {
      return null;
    }

    return new RoadMapItemDto(
        item.getId(),
        item.getTitle(),
        item.getDescription(),
        item.getStatus(),
        item.getTargetDate()
    );
  }

  /**
   * Converts DTO to model.
   *
   * @param dto API DTO
   * @return model representation
   */
  public static RoadMapItem toEntity(RoadMapItemDto dto) {
    if (dto == null) {
      return null;
    }

    return new RoadMapItem(
        dto.getId(),
        dto.getTitle(),
        dto.getDescription(),
        dto.getStatus(),
        dto.getTargetDate()
    );
  }

  /**
   * Converts list of model entities to list of DTOs.
   *
   * @param items model list
   * @return DTO list
   */
  public static List<RoadMapItemDto> toDtoList(List<RoadMapItem> items) {
    if (items == null) {
      return List.of();
    }

    List<RoadMapItemDto> dtos = new ArrayList<>();
    for (RoadMapItem item : items) {
      dtos.add(toDto(item));
    }
    return dtos;
  }
}
