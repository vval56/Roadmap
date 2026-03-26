package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapDto;
import com.example.roadmap.service.RoadMapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roadmaps")
@RequiredArgsConstructor
@Validated
@Tag(name = "Roadmaps", description = "CRUD operations for roadmaps")
public class RoadMapController {

  private final RoadMapService roadMapService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create roadmap")
  public RoadMapDto create(@Valid @RequestBody RoadMapDto dto) {
    return roadMapService.create(dto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get roadmap by id")
  public RoadMapDto getById(@Parameter(description = "Roadmap id", example = "1")
                            @PathVariable @Positive Long id) {
    return roadMapService.getById(id);
  }

  @GetMapping(params = "title")
  @Operation(summary = "Get roadmap by title")
  public RoadMapDto getByTitle(@Parameter(description = "Roadmap title", example = "Java Backend 2026")
                               @RequestParam @NotBlank String title) {
    return roadMapService.getByTitle(title);
  }

  @GetMapping
  @Operation(summary = "Get all roadmaps")
  public List<RoadMapDto> getAll() {
    return roadMapService.getAll();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update roadmap")
  public RoadMapDto update(@Parameter(description = "Roadmap id", example = "1")
                           @PathVariable @Positive Long id,
                           @Valid @RequestBody RoadMapDto dto) {
    return roadMapService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Patch roadmap")
  public RoadMapDto patch(@Parameter(description = "Roadmap id", example = "1")
                          @PathVariable @Positive Long id,
                          @Valid @RequestBody RoadMapDto dto) {
    return roadMapService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete roadmap")
  public void delete(@Parameter(description = "Roadmap id", example = "1")
                     @PathVariable @Positive Long id) {
    roadMapService.delete(id);
  }
}
