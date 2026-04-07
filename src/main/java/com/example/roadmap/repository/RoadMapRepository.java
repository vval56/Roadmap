package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {

  List<RoadMap> findByTitle(String title);

  @EntityGraph(attributePaths = {"owner", "items", "items.tags", "items.comments"})
  Optional<RoadMap> findDetailedById(Long id);
}
