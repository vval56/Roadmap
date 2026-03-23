package com.example.roadmap.dto;

import com.example.roadmap.model.User;

public final class UserMapper {

  private UserMapper() {
  }

    public static UserDto toDto(User entity) {
    UserDto dto = new UserDto();
    dto.setId(entity.getId());
    dto.setFirstName(entity.getFirstName());
    dto.setLastName(entity.getLastName());
    dto.setEmail(entity.getEmail());
    return dto;
  }

    public static void copyToEntity(UserDto dto, User entity) {
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmail(dto.getEmail());
  }
}
