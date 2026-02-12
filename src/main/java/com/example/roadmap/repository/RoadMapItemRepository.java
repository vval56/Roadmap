package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;

import java.util.List;

public interface RoadMapItemRepository {
    List<RoadMapItem> findAll();

    RoadMapItem findById(Long id);

    List<RoadMapItem> findByStatus(String status);

    List<RoadMapItem> findByPriority(String priority);

    RoadMapItem save(RoadMapItem item);

    void deleteById(Long id);
}
