package com.example.roadmap.controller;

import com.example.roadmap.dto.TagDto;
import com.example.roadmap.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tags", description = "CRUD operations for tags")
public class TagController {

  private final TagService tagService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create tag", description = "Creates a new tag")
  public TagDto create(@Valid @RequestBody TagDto dto) {
    return tagService.create(dto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get tag by id")
  public TagDto getById(@Parameter(description = "Tag id", example = "2")
                        @PathVariable @Positive Long id) {
    return tagService.getById(id);
  }

  @GetMapping
  @Operation(summary = "Get all tags")
  public List<TagDto> getAll() {
    return tagService.getAll();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update tag", description = "Replaces tag data by id")
  public TagDto update(@Parameter(description = "Tag id", example = "2")
                       @PathVariable @Positive Long id,
                       @Valid @RequestBody TagDto dto) {
    return tagService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Patch tag", description = "Updates tag by id")
  public TagDto patch(@Parameter(description = "Tag id", example = "2")
                      @PathVariable @Positive Long id,
                      @Valid @RequestBody TagDto dto) {
    return tagService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete tag")
  public void delete(@Parameter(description = "Tag id", example = "2")
                     @PathVariable @Positive Long id) {
    tagService.delete(id);
  }
}
