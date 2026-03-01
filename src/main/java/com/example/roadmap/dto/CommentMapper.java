package com.example.roadmap.dto;

import com.example.roadmap.model.Comment;

/**
 * Mapper for comment DTOs.
 */
public final class CommentMapper {

  private CommentMapper() {
  }

  /**
   * Maps comment entity to DTO.
   *
   * @param entity comment entity
   * @return mapped DTO
   */
  public static CommentDto toDto(Comment entity) {
    CommentDto dto = new CommentDto();
    dto.setId(entity.getId());
    dto.setContent(entity.getContent());
    dto.setItemId(entity.getItem().getId());
    dto.setAuthorId(entity.getAuthor().getId());
    return dto;
  }

  /**
   * Copies DTO fields to entity.
   *
   * @param dto source DTO
   * @param entity target entity
   */
  public static void copyToEntity(CommentDto dto, Comment entity) {
    entity.setContent(dto.getContent());
  }
}
