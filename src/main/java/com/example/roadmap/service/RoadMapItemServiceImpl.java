package com.example.roadmap.service;

import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.repository.RoadMapItemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Default implementation of RoadMapItemService.
 */
@Service
public class RoadMapItemServiceImpl implements RoadMapItemService {

  private final RoadMapItemRepository roadMapItemRepository;

  public RoadMapItemServiceImpl(RoadMapItemRepository roadMapItemRepository) {
    this.roadMapItemRepository = roadMapItemRepository;
  }

  @Override
  public List<RoadMapItem> getAllItems() {
    return roadMapItemRepository.findAll();
  }

  @Override
  public RoadMapItem getItemById(Long id) {
    return roadMapItemRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "RoadMap item with id=" + id + " was not found"
        ));
  }

  @Override
  public List<RoadMapItem> getItemsByStatus(String status) {
    return roadMapItemRepository.findByStatus(status);
  }

  @Override
  public RoadMapItem createItem(RoadMapItem item) {
    item.setId(null);
    return roadMapItemRepository.save(item);
  }
}
