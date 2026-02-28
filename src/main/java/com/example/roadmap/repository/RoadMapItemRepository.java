package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;
import java.util.List;
import java.util.Optional;

/**
 * Repository contract for roadmap items.
 */
public interface RoadMapItemRepository {

  List<RoadMapItem> findAll();

  Optional<RoadMapItem> findById(Long id);

  List<RoadMapItem> findByStatus(String status);

  RoadMapItem save(RoadMapItem item);

  void deleteById(Long id);
}
