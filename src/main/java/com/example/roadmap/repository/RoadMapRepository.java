package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap, Long> {
    List<RoadMap> findByUserId(Long userId);
}