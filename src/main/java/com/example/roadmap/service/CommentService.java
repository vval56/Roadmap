package com.example.roadmap.service;

import com.example.roadmap.dto.CommentDto;
import java.util.List;

public interface CommentService {

  CommentDto create(CommentDto dto);

  CommentDto getById(Long id);

  List<CommentDto> getAll();

  CommentDto update(Long id, CommentDto dto);

  void delete(Long id);
}
