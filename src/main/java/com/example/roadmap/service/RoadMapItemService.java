package com.example.roadmap.service;

import com.example.roadmap.model.RoadMapItem;
import java.util.List;

/**
 * Service contract for business logic around roadmap items.
 */
public interface RoadMapItemService {

  List<RoadMapItem> getAllItems();

  RoadMapItem getItemById(Long id);

  List<RoadMapItem> getItemsByStatus(String status);

  RoadMapItem createItem(RoadMapItem item);
}
