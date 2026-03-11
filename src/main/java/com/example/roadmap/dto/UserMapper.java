package com.example.roadmap.dto;

import com.example.roadmap.model.User;

/**
 * Mapper for user DTOs.
 */
public final class UserMapper {

  private UserMapper() {
  }

  /**
   * Maps user entity to DTO.
   *
   * @param entity user entity
   * @return mapped DTO
   */
  public static UserDto toDto(User entity) {
    UserDto dto = new UserDto();
    dto.setId(entity.getId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmail(entity.getEmail());
    return dto;
  }

  /**
   * Copies DTO fields to entity.
   *
   * @param dto source DTO
   * @param entity target entity
   */
  public static void copyToEntity(UserDto dto, User entity) {
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmail(dto.getEmail());
  }
}
