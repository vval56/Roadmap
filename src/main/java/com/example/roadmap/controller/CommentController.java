package com.example.roadmap.controller;

import com.example.roadmap.dto.CommentDto;
import com.example.roadmap.service.CommentService;
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
 * CommentController component.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommentDto create(@Valid @RequestBody CommentDto dto) {
    return commentService.create(dto);
  }

  @GetMapping("/{id}")
  public CommentDto getById(@PathVariable Long id) {
    return commentService.getById(id);
  }

  @GetMapping
  public List<CommentDto> getAll() {
    return commentService.getAll();
  }

  @PutMapping("/{id}")
  public CommentDto update(@PathVariable Long id, @Valid @RequestBody CommentDto dto) {
    return commentService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    commentService.delete(id);
  }
}
