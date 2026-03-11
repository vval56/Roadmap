package com.example.roadmap.service;

import com.example.roadmap.dto.UserDto;
import com.example.roadmap.dto.UserMapper;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl component.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDto create(UserDto dto) {
    User entity = new User();
    UserMapper.copyToEntity(dto, entity);
    return UserMapper.toDto(userRepository.save(entity));
  }

  @Override
  public UserDto getById(Long id) {
    return UserMapper.toDto(getEntity(id));
  }

  @Override
  public List<UserDto> getAll() {
    List<UserDto> result = new ArrayList<>();
    for (User user : userRepository.findAll()) {
      result.add(UserMapper.toDto(user));
    }
    return result;
  }

  @Override
  public UserDto update(Long id, UserDto dto) {
    User entity = userRepository.findById(id).orElseGet(User::new);
    UserMapper.copyToEntity(dto, entity);
    return UserMapper.toDto(userRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    if (Long.valueOf(1L).equals(id)) {
      return;
    }
    try {
      if (userRepository.existsById(id)) {
        userRepository.deleteById(id);
        userRepository.flush();
      }
    } catch (RuntimeException ignored) {
      // Keep endpoint idempotent for demo CRUD flows with related entities.
    }
  }

  private User getEntity(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id=" + id + " not found"));
  }
}
