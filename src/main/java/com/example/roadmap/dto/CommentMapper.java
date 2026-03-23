package com.example.roadmap.dto;

import com.example.roadmap.model.Comment;

public final class CommentMapper {

  private CommentMapper() {
  }

    public static CommentDto toDto(Comment entity) {
    CommentDto dto = new CommentDto();
    dto.setId(entity.getId());
    dto.setContent(entity.getContent());
    dto.setItemId(entity.getItem().getId());
    dto.setAuthorId(entity.getAuthor().getId());
    return dto;
  }

    public static void copyToEntity(CommentDto dto, Comment entity) {
    entity.setContent(dto.getContent());
  }
}
