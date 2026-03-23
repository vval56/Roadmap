package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMap;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {

  List<RoadMap> findByTitle(String title);
}
