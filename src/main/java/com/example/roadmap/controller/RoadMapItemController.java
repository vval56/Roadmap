package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.service.RoadMapItemService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoadMapItemController component.
 */
@RestController
@RequestMapping("/api/roadmap-items")
@RequiredArgsConstructor
public class RoadMapItemController {

  private final RoadMapItemService roadMapItemService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RoadMapItemDto create(@Valid @RequestBody RoadMapItemDto dto) {
    return roadMapItemService.create(dto);
  }

  @GetMapping("/{id}")
  public RoadMapItemDto getById(@PathVariable Long id) {
    return roadMapItemService.getById(id);
  }

  @GetMapping
  public List<RoadMapItemDto> getAll() {
    return roadMapItemService.getAll();
  }

  @PutMapping("/{id}")
  public RoadMapItemDto update(@PathVariable Long id, @Valid @RequestBody RoadMapItemDto dto) {
    return roadMapItemService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    roadMapItemService.delete(id);
  }

  @GetMapping("/n-plus-one")
  public List<RoadMapItemWithTagsDto> nplusOne() {
    return roadMapItemService.getAllWithNplusOne();
  }

  @GetMapping("/entity-graph")
  public List<RoadMapItemWithTagsDto> optimizedWithEntityGraph() {
    return roadMapItemService.getAllWithEntityGraph();
  }

  @GetMapping("/fetch-join")
  public List<RoadMapItemWithTagsDto> optimizedWithFetchJoin() {
    return roadMapItemService.getAllWithFetchJoin();
  }
}
