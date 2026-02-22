package com.example.roadmap.service;

import com.example.roadmap.model.Comment;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.RoadMapItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoadMapItemServiceImpl implements RoadMapItemService {

    private final RoadMapItemRepository repository;

    public RoadMapItemServiceImpl(RoadMapItemRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getAllItems() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RoadMapItem getItemById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getItemsByStatus(String status) {
        return repository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getItemsByPriority(String priority) {
        return repository.findByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getItemsByRoadMapId(Long roadMapId) {
        return repository.findByRoadMapId(roadMapId);
    }

    @Override
    @Transactional
    public RoadMapItem createItem(RoadMapItem item) {
        return repository.save(item);
    }

    @Override
    @Transactional
    public RoadMapItem updateItem(Long id, RoadMapItem item) {
        RoadMapItem existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(item.getTitle());
            existing.setDescription(item.getDescription());
            existing.setStatus(item.getStatus());
            existing.setPriority(item.getPriority());
            existing.setStartDate(item.getStartDate());
            existing.setTargetDate(item.getTargetDate());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getAllWithTagsNPlus1() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMapItem> getAllWithTagsOptimized() {
        return repository.findAllWithTags();
    }

    @Override
    @Transactional
    public void saveItemWithTagsAndCommentsTransactional(RoadMapItem item, List<Tag> tags, List<Comment> comments) {
        item.setTags(tags);
        item.setComments(comments);
        for (Tag tag : tags) {
            tag.getItems().add(item);
        }
        for (Comment comment : comments) {
            comment.setRoadMapItem(item);
        }
        repository.save(item);
        throw new RuntimeException("Test error: demonstrating rollback");
    }

    @Override
    public void saveItemWithTagsNoTransaction(RoadMapItem item, List<Tag> tags) {
        item.setTags(tags);
        for (Tag tag : tags) {
            tag.getItems().add(item);
        }
        repository.save(item);
        throw new RuntimeException("Test error: no rollback");
    }
}