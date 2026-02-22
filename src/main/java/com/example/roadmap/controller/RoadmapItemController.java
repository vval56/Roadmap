package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapItemDTO;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.model.Comment;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.Tag;
import com.example.roadmap.service.RoadMapItemService;
import com.example.roadmap.service.RoadMapItemServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmap-items")
public class RoadmapItemController {

    private final RoadMapItemService service;

    public RoadmapItemController(RoadMapItemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RoadMapItemDTO>> getAllItems() {
        List<RoadMapItem> items = service.getAllItems();
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadMapItemDTO> getItemById(@PathVariable Long id) {
        RoadMapItem item = service.getItemById(id);
        return item != null ? ResponseEntity.ok(RoadMapItemMapper.toDTO(item)) : ResponseEntity.notFound().build();
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<RoadMapItemDTO>> getItemsByStatus(@RequestParam String status) {
        List<RoadMapItem> items = service.getItemsByStatus(status);
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(params = "priority")
    public ResponseEntity<List<RoadMapItemDTO>> getItemsByPriority(@RequestParam String priority) {
        List<RoadMapItem> items = service.getItemsByPriority(priority);
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/roadmap/{roadMapId}")
    public ResponseEntity<List<RoadMapItemDTO>> getItemsByRoadMapId(@PathVariable Long roadMapId) {
        List<RoadMapItem> items = service.getItemsByRoadMapId(roadMapId);
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<RoadMapItemDTO> createItem(@RequestBody RoadMapItemDTO dto) {
        RoadMapItem entity = RoadMapItemMapper.toEntity(dto);
        RoadMapItem created = service.createItem(entity);
        return ResponseEntity.ok(RoadMapItemMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadMapItemDTO> updateItem(@PathVariable Long id, @RequestBody RoadMapItemDTO dto) {
        RoadMapItem entity = RoadMapItemMapper.toEntity(dto);
        RoadMapItem updated = service.updateItem(id, entity);
        return updated != null ? ResponseEntity.ok(RoadMapItemMapper.toDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // N+1 демонстрация
    @GetMapping("/with-tags-nplus1")
    public ResponseEntity<List<RoadMapItemDTO>> getAllWithTagsNPlus1() {
        List<RoadMapItem> items = ((RoadMapItemServiceImpl) service).getAllWithTagsNPlus1();
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/with-tags-optimized")
    public ResponseEntity<List<RoadMapItemDTO>> getAllWithTagsOptimized() {
        List<RoadMapItem> items = ((RoadMapItemServiceImpl) service).getAllWithTagsOptimized();
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    // Transactional демонстрация
    @PostMapping("/with-rollback")
    public ResponseEntity<Void> createWithRollback(@RequestBody RoadMapItemDTO dto) {
        RoadMapItem entity = RoadMapItemMapper.toEntity(dto);
        try {
            ((RoadMapItemServiceImpl) service).saveItemWithTagsAndCommentsTransactional(
                    entity,
                    List.of(new Tag("test")),
                    List.of(new Comment())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }
}