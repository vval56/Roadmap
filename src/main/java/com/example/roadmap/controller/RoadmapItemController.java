package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapItemDTO;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.service.RoadMapItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roadmap-items")
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
        RoadMapItemDTO dto = RoadMapItemMapper.toDTO(item);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<RoadMapItemDTO>> getItemByStatus(@RequestParam String status) {
        List<RoadMapItem> items = service.getItemsByStatus(status);
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(params = "priority")
    public ResponseEntity<List<RoadMapItemDTO>> getItemByPriority(@RequestParam String priority) {
        List<RoadMapItem> items = service.getItemsByPriority(priority);
        List<RoadMapItemDTO> dtos = RoadMapItemMapper.toDTOList(items);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<RoadMapItemDTO> createItem(@RequestBody RoadMapItemDTO dto) {
        RoadMapItem entity = RoadMapItemMapper.toEntity(dto);
        RoadMapItem createdItem = service.createItem(entity);
        RoadMapItemDTO createdDto = RoadMapItemMapper.toDTO(createdItem);
        return ResponseEntity.ok(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadMapItemDTO> updateItem(@PathVariable Long id, @RequestBody RoadMapItemDTO dto) {
        RoadMapItem entity = RoadMapItemMapper.toEntity(dto);
        RoadMapItem updatedItem = service.updateItem(id, entity);
        RoadMapItemDTO updatedDto = RoadMapItemMapper.toDTO(updatedItem);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}