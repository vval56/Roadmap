package com.example.roadmap.service;

import com.example.roadmap.model.Comment;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import java.util.List;

public interface RoadMapItemService {
    List<RoadMapItem> getAllItems();
    RoadMapItem getItemById(Long id);
    List<RoadMapItem> getItemsByStatus(String status);
    List<RoadMapItem> getItemsByPriority(String priority);
    List<RoadMapItem> getItemsByRoadMapId(Long roadMapId);
    RoadMapItem createItem(RoadMapItem item);
    RoadMapItem updateItem(Long id, RoadMapItem item);
    void deleteItem(Long id);

    // N+1 демонстрация
    List<RoadMapItem> getAllWithTagsNPlus1();
    List<RoadMapItem> getAllWithTagsOptimized();

    // Transactional демонстрация
    void saveItemWithTagsAndCommentsTransactional(RoadMapItem item, List<Tag> tags, List<Comment> comments);
    void saveItemWithTagsNoTransaction(RoadMapItem item, List<Tag> tags);
}