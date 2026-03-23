package com.example.roadmap.controller;

import com.example.roadmap.dto.TagDto;
import com.example.roadmap.service.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class TagController {

  private final TagService tagService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TagDto create(@Valid @RequestBody TagDto dto) {
    return tagService.create(dto);
  }

  @GetMapping("/{id}")
  public TagDto getById(@PathVariable Long id) {
    return tagService.getById(id);
  }

  @GetMapping
  public List<TagDto> getAll() {
    return tagService.getAll();
  }

  @PutMapping("/{id}")
  public TagDto update(@PathVariable Long id, @Valid @RequestBody TagDto dto) {
    return tagService.update(id, dto);
  }

  @PatchMapping("/{id}")
  public TagDto patch(@PathVariable Long id, @Valid @RequestBody TagDto dto) {
    return tagService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    tagService.delete(id);
  }
}
