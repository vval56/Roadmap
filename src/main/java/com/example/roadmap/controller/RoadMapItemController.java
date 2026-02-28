package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemMapper;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.service.RoadMapItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for roadmap item endpoints.
 */
@RestController
@RequestMapping("/api/items")
public class RoadMapItemController {

  private final RoadMapItemService roadMapItemService;

  public RoadMapItemController(RoadMapItemService roadMapItemService) {
    this.roadMapItemService = roadMapItemService;
  }

  @GetMapping
  public List<RoadMapItemDto> getAllItems() {
    return RoadMapItemMapper.toDtoList(roadMapItemService.getAllItems());
  }

  @GetMapping(params = "status")
  public List<RoadMapItemDto> getItemsByStatus(@RequestParam String status) {
    return RoadMapItemMapper.toDtoList(roadMapItemService.getItemsByStatus(status));
  }

  @GetMapping("/{id}")
  public RoadMapItemDto getItemById(@PathVariable Long id) {
    RoadMapItem item = roadMapItemService.getItemById(id);
    return RoadMapItemMapper.toDto(item);
  }

  /**
   * Creates a new roadmap item.
   *
   * @param dto API request body
   * @return created item DTO
   */
  @PostMapping
  public RoadMapItemDto createItem(@Valid @RequestBody RoadMapItemDto dto) {
    RoadMapItem item = RoadMapItemMapper.toEntity(dto);
    RoadMapItem createdItem = roadMapItemService.createItem(item);
    return RoadMapItemMapper.toDto(createdItem);
  }
}
