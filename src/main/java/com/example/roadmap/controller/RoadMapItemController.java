package com.example.roadmap.controller;

import com.example.roadmap.dto.PageResponseDto;
import com.example.roadmap.dto.RoadMapItemBulkCreateDto;
import com.example.roadmap.dto.RoadMapItemDto;
import com.example.roadmap.dto.RoadMapItemWithTagsDto;
import com.example.roadmap.model.ItemStatus;
import com.example.roadmap.service.RoadMapItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/roadmap-items")
@RequiredArgsConstructor
@Validated
@Tag(name = "Roadmap Items", description = "CRUD, pagination and search operations for roadmap items")
public class RoadMapItemController {

  private final RoadMapItemService roadMapItemService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create roadmap item")
  public RoadMapItemDto create(@Valid @RequestBody RoadMapItemDto dto) {
    return roadMapItemService.create(dto);
  }

  @PostMapping("/bulk/{roadMapId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Bulk create roadmap items",
      description = "Creates multiple roadmap items for one roadmap in a single request")
  public List<RoadMapItemDto> createBulk(
      @Parameter(description = "Roadmap id that will own all created items", example = "1")
      @PathVariable @Positive Long roadMapId,
      @Valid @RequestBody @NotEmpty List<@Valid RoadMapItemBulkCreateDto> dtos) {
    return roadMapItemService.createBulk(roadMapId, dtos);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get roadmap item by id")
  public RoadMapItemDto getById(@Parameter(description = "Roadmap item id", example = "10")
                                @PathVariable @Positive Long id) {
    return roadMapItemService.getById(id);
  }

  @GetMapping
  @Operation(summary = "Get all roadmap items")
  public List<RoadMapItemDto> getAll() {
    return roadMapItemService.getAll();
  }

  @GetMapping("/page")
  @Operation(summary = "Get roadmap items page")
  public PageResponseDto<RoadMapItemDto> getPage(
      @ParameterObject @PageableDefault(size = 5) Pageable pageable) {
    return PageResponseDto.from(roadMapItemService.getPage(pageable));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update roadmap item")
  public RoadMapItemDto update(@Parameter(description = "Roadmap item id", example = "10")
                               @PathVariable @Positive Long id,
                               @Valid @RequestBody RoadMapItemDto dto) {
    return roadMapItemService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Patch roadmap item")
  public RoadMapItemDto patch(@Parameter(description = "Roadmap item id", example = "10")
                              @PathVariable @Positive Long id,
                              @Valid @RequestBody RoadMapItemDto dto) {
    return roadMapItemService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete roadmap item")
  public void delete(@Parameter(description = "Roadmap item id", example = "10")
                     @PathVariable @Positive Long id) {
    roadMapItemService.delete(id);
  }

  @GetMapping("/n-plus-one")
  @Operation(summary = "Demonstrate N+1 problem")
  public List<RoadMapItemWithTagsDto> nplusOne() {
    return roadMapItemService.getAllWithNplusOne();
  }

  @GetMapping("/entity-graph")
  @Operation(summary = "Get roadmap items using EntityGraph optimization")
  public List<RoadMapItemWithTagsDto> optimizedWithEntityGraph() {
    return roadMapItemService.getAllWithEntityGraph();
  }

  @GetMapping("/fetch-join")
  @Operation(summary = "Get roadmap items using fetch join optimization")
  public List<RoadMapItemWithTagsDto> optimizedWithFetchJoin() {
    return roadMapItemService.getAllWithFetchJoin();
  }

  @GetMapping("/search/jpql")
  @Operation(summary = "Search roadmap items with JPQL")
  public List<RoadMapItemDto> searchWithJpql(
      @Parameter(description = "Owner email filter", example = "vladislav@example.com")
      @RequestParam(required = false) String ownerEmail,
      @Parameter(description = "Roadmap title filter", example = "java")
      @RequestParam(required = false) String roadMapTitle,
      @Parameter(description = "Parent item title filter", example = "jpa")
      @RequestParam(required = false) String parentTitle,
      @Parameter(description = "Tag name filter", example = "spring")
      @RequestParam(required = false) String tagName,
      @Parameter(description = "Status filter", example = "IN_PROGRESS")
      @RequestParam(required = false) ItemStatus status) {
    return roadMapItemService.searchWithJpql(
        ownerEmail, roadMapTitle, parentTitle, tagName, status);
  }

  @GetMapping("/search/native")
  @Operation(summary = "Search roadmap items with native SQL")
  public List<RoadMapItemDto> searchWithNative(
      @Parameter(description = "Owner email filter", example = "vladislav@example.com")
      @RequestParam(required = false) String ownerEmail,
      @Parameter(description = "Roadmap title filter", example = "java")
      @RequestParam(required = false) String roadMapTitle,
      @Parameter(description = "Parent item title filter", example = "jpa")
      @RequestParam(required = false) String parentTitle,
      @Parameter(description = "Tag name filter", example = "spring")
      @RequestParam(required = false) String tagName,
      @Parameter(description = "Status filter", example = "IN_PROGRESS")
      @RequestParam(required = false) ItemStatus status) {
    return roadMapItemService.searchWithNative(
        ownerEmail, roadMapTitle, parentTitle, tagName, status);
  }
}
