package com.example.roadmap.dto;

import com.example.roadmap.model.Tag;

public final class TagMapper {

  private TagMapper() {
  }

    public static TagDto toDto(Tag entity) {
    TagDto dto = new TagDto();
    dto.setId(entity.getId());
    dto.setName(entity.getName());
    return dto;
  }

    public static void copyToEntity(TagDto dto, Tag entity) {
    entity.setName(dto.getName());
  }
}
