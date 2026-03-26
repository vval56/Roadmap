package com.example.roadmap.controller;

import com.example.roadmap.dto.CommentDto;
import com.example.roadmap.service.CommentService;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Comments", description = "CRUD operations for comments")
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create comment")
  public CommentDto create(@Valid @RequestBody CommentDto dto) {
    return commentService.create(dto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get comment by id")
  public CommentDto getById(@Parameter(description = "Comment id", example = "4")
                            @PathVariable @Positive Long id) {
    return commentService.getById(id);
  }

  @GetMapping
  @Operation(summary = "Get all comments")
  public List<CommentDto> getAll() {
    return commentService.getAll();
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update comment")
  public CommentDto update(@Parameter(description = "Comment id", example = "4")
                           @PathVariable @Positive Long id,
                           @Valid @RequestBody CommentDto dto) {
    return commentService.update(id, dto);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Patch comment")
  public CommentDto patch(@Parameter(description = "Comment id", example = "4")
                          @PathVariable @Positive Long id,
                          @Valid @RequestBody CommentDto dto) {
    return commentService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete comment")
  public void delete(@Parameter(description = "Comment id", example = "4")
                     @PathVariable @Positive Long id) {
    commentService.delete(id);
  }
}
