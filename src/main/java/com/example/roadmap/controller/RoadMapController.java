package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapDto;
import com.example.roadmap.service.RoadMapService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * RoadMapController component.
 */
@RestController
@RequestMapping("/api/roadmaps")
@RequiredArgsConstructor
public class RoadMapController {

  private final RoadMapService roadMapService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RoadMapDto create(@Valid @RequestBody RoadMapDto dto) {
    return roadMapService.create(dto);
  }

  @GetMapping("/{id}")
  public RoadMapDto getById(@PathVariable Long id) {
    return roadMapService.getById(id);
  }

  @GetMapping(params = "title")
  public RoadMapDto getByTitle(@RequestParam String title) {
    return roadMapService.getByTitle(title);
  }

  @GetMapping
  public List<RoadMapDto> getAll() {
    return roadMapService.getAll();
  }

  @PutMapping("/{id}")
  public RoadMapDto update(@PathVariable Long id, @Valid @RequestBody RoadMapDto dto) {
    return roadMapService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    roadMapService.delete(id);
  }
}
