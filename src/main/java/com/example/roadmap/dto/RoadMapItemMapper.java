package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class RoadMapItemMapper {

  private RoadMapItemMapper() {
  }

  public static RoadMapItemDto toDto(RoadMapItem entity) {
    RoadMapItemDto dto = new RoadMapItemDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setDetails(entity.getDetails());
    dto.setStatus(entity.getStatus());
    dto.setRoadMapId(entity.getRoadMap().getId());
    dto.setParentItemId(entity.getParentItem() != null ? entity.getParentItem().getId() : null);

    LinkedHashSet<Long> tagIds = new LinkedHashSet<>();
    for (Tag tag : entity.getTags()) {
      tagIds.add(tag.getId());
    }
    dto.setTagIds(tagIds);
    return dto;
  }

  public static RoadMapItemDto toDtoWithoutTags(RoadMapItem entity) {
    RoadMapItemDto dto = new RoadMapItemDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setDetails(entity.getDetails());
    dto.setStatus(entity.getStatus());
    dto.setRoadMapId(entity.getRoadMap().getId());
    dto.setParentItemId(entity.getParentItem() != null ? entity.getParentItem().getId() : null);
    return dto;
  }

  public static void copyToEntity(RoadMapItemDto dto, RoadMapItem entity) {
    entity.setTitle(dto.getTitle());
    entity.setDetails(dto.getDetails());
    entity.setStatus(dto.getStatus());
  }

  public static RoadMapItemWithTagsDto toWithTagsDto(RoadMapItem entity) {
    RoadMapItemWithTagsDto dto = new RoadMapItemWithTagsDto();
    dto.setId(entity.getId());
    dto.setTitle(entity.getTitle());
    dto.setStatus(entity.getStatus());

    List<String> tags = new ArrayList<>();
    for (Tag tag : entity.getTags()) {
      tags.add(tag.getName());
    }
    dto.setTags(tags);
    return dto;
  }
}
