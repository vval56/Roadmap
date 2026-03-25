package com.example.roadmap.service;

import com.example.roadmap.cache.RoadMapItemSearchIndexService;
import com.example.roadmap.dto.RoadMapDto;
import com.example.roadmap.dto.RoadMapMapper;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.RoadMapRepository;
import com.example.roadmap.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoadMapServiceImpl implements RoadMapService {

  private final RoadMapRepository roadMapRepository;
  private final UserRepository userRepository;
  private final RoadMapItemSearchIndexService searchIndexService;

  @Override
  public RoadMapDto create(RoadMapDto dto) {
    RoadMap entity = new RoadMap();
    RoadMapMapper.copyToEntity(dto, entity);
    entity.setOwner(getOwner(dto.getOwnerId()));
    RoadMapDto saved = RoadMapMapper.toDto(roadMapRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
  }

  @Override
  public RoadMapDto getById(Long id) {
    return RoadMapMapper.toDto(getEntity(id));
  }

  @Override
  public RoadMapDto getByTitle(String title) {
    List<RoadMap> roadMaps = roadMapRepository.findByTitle(title);
    if (roadMaps.isEmpty()) {
      throw new ResourceNotFoundException("RoadMap with title=" + title + " not found");
    }
    return RoadMapMapper.toDto(roadMaps.getFirst());
  }

  @Override
  public List<RoadMapDto> getAll() {
    List<RoadMapDto> result = new ArrayList<>();
    for (RoadMap roadMap : roadMapRepository.findAll()) {
      result.add(RoadMapMapper.toDto(roadMap));
    }
    return result;
  }

  @Override
  public RoadMapDto update(Long id, RoadMapDto dto) {
    RoadMap entity = getEntity(id);
    RoadMapMapper.copyToEntity(dto, entity);
    entity.setOwner(getOwner(dto.getOwnerId()));
    RoadMapDto saved = RoadMapMapper.toDto(roadMapRepository.save(entity));
    searchIndexService.invalidateAll();
    return saved;
  }

  @Override
  public void delete(Long id) {
    if (roadMapRepository.existsById(id)) {
      roadMapRepository.deleteById(id);
      searchIndexService.invalidateAll();
    }
  }

  private RoadMap getEntity(Long id) {
    return roadMapRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("RoadMap with id=" + id + " not found"));
  }

  private User getOwner(Long id) {
    return userRepository.findById(id).orElseGet(this::getAnyUser);
  }

  private User getAnyUser() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      throw new ResourceNotFoundException("Owner with id not found");
    }
    return users.getFirst();
  }
}
