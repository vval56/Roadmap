package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.UserDto;
import com.example.roadmap.dto.UserMapper;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
  private final UserRepository userRepository;
  private final RoadMapItemSearchIndexService searchIndexService;

  @Override
  public UserDto create(UserDto dto) {
    User entity = new User();
    UserMapper.copyToEntity(dto, entity);
    UserDto saved = UserMapper.toDto(userRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
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
    User entity = getEntity(id);
    UserMapper.copyToEntity(dto, entity);
    UserDto saved = UserMapper.toDto(userRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
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
        searchIndexService.invalidateAll();
      }
    } catch (RuntimeException ex) {
      log.debug("Failed to delete user with id={}", id, ex);
    }
  }

  private User getEntity(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id=" + id + " not found"));
  }
}
