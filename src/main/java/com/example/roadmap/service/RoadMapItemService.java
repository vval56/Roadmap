package com.example.roadmap.service;

import com.example.roadmap.model.RoadMapItem;

import java.util.List;

public interface RoadMapItemService {
    List<RoadMapItem> getAllItems();

    RoadMapItem getItemById(Long id);

    List<RoadMapItem> getItemsByStatus(String status);

    List<RoadMapItem> getItemsByPriority(String priority);

    RoadMapItem createItem(RoadMapItem item);

    RoadMapItem updateItem(Long id, RoadMapItem item);

    void deleteItem(Long id);
}
