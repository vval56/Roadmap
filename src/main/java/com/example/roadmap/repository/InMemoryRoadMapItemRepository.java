package com.example.roadmap.repository;

import com.example.roadmap.model.RoadMapItem;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryRoadMapItemRepository implements RoadMapItemRepository {
    private final List<RoadMapItem> items = new ArrayList<>();

    @Override
    public List<RoadMapItem> findAll() {
        return items;
    }

    @Override
    public RoadMapItem findById(Long id) {
        for (RoadMapItem item : items) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public List<RoadMapItem> findByStatus(String status) {
        List<RoadMapItem> result = new ArrayList<>();
        for (RoadMapItem item : items) {
            if (item.getStatus().equals(status)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<RoadMapItem> findByPriority(String priority) {
        List<RoadMapItem> result = new ArrayList<>();
        for (RoadMapItem item : items) {
            if (item.getPriority().equals(priority)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public RoadMapItem save(@NonNull RoadMapItem item) {
        if (item.getId() == null) {
            Long newId = (long) (items.size() + 1);
            item.setId(newId);
            items.add(item);
        } else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equals(item.getId())) {
                    items.set(i, item);
                    break;
                }
            }
        }
        return item;
    }

    @Override
    public void deleteById(Long id) {
        items.removeIf(item -> item.getId().equals(id));
    }
}