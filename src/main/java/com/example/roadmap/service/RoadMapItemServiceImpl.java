package com.example.roadmap.service;

import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.repository.RoadMapItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoadMapItemServiceImpl implements RoadMapItemService {
    private final RoadMapItemRepository repository;

    public RoadMapItemServiceImpl(RoadMapItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<RoadMapItem> getAllItems() {
        return repository.findAll();
    }

    @Override
    public RoadMapItem getItemById(Long id) {
        RoadMapItem item = repository.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found with id:" + id);
        }
        return item;
    }

    @Override
    public List<RoadMapItem> getItemsByStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return repository.findByStatus(status);
    }

    @Override
    public List<RoadMapItem> getItemsByPriority(String priority) {
        if (priority == null || priority.isEmpty()) {
            throw new IllegalArgumentException("Priority cannot be empty");
        }
        return repository.findByPriority(priority);
    }

    @Override
    public RoadMapItem createItem(RoadMapItem item) {
        validateItem(item);
        return repository.save(item);
    }

    @Override
    public RoadMapItem updateItem(Long id, RoadMapItem item) {
        RoadMapItem existingItem = repository.findById(id);
        if (existingItem == null) {
            throw new IllegalArgumentException("Item not found with id:" + id);
        }

        item.setId(id);
        validateItem(item);
        return repository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        RoadMapItem item = repository.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found with id:" + id);
        }

        if ("COMPLETED".equals(item.getStatus())) {
            throw new IllegalStateException("Cannot delete completed task");
        }

        repository.deleteById(id);
    }

    private void validateItem(RoadMapItem item) {
        if (item.getTitle() == null || item.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        String status = item.getStatus();
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        String priority = item.getPriority();
        if (priority == null || priority.isEmpty()) {
            throw new IllegalArgumentException("Priority cannot be empty");
        }
    }
}
