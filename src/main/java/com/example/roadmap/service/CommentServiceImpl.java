package com.example.roadmap.service;

import com.example.roadmap.dto.CommentDto;
import com.example.roadmap.dto.CommentMapper;
import com.example.roadmap.exception.ResourceNotFoundException;
import com.example.roadmap.model.Comment;
import com.example.roadmap.model.RoadMapItem;
import com.example.roadmap.model.User;
import com.example.roadmap.repository.CommentRepository;
import com.example.roadmap.repository.RoadMapItemRepository;
import com.example.roadmap.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

  private static final String NOT_FOUND_SUFFIX = " not found";

  private final CommentRepository commentRepository;
  private final RoadMapItemRepository roadMapItemRepository;
  private final UserRepository userRepository;

  @Override
  public CommentDto create(CommentDto dto) {
    Comment entity = new Comment();
    CommentMapper.copyToEntity(dto, entity);
    entity.setItem(getItem(dto.getItemId()));
    entity.setAuthor(getAuthor(dto.getAuthorId()));
    return CommentMapper.toDto(commentRepository.save(entity));
  }

  @Override
  public CommentDto getById(Long id) {
    return CommentMapper.toDto(getEntity(id));
  }

  @Override
  public List<CommentDto> getAll() {
    List<CommentDto> result = new ArrayList<>();
    for (Comment comment : commentRepository.findAll()) {
      result.add(CommentMapper.toDto(comment));
    }
    return result;
  }

  @Override
  public CommentDto update(Long id, CommentDto dto) {
    Comment entity = getEntity(id);
    CommentMapper.copyToEntity(dto, entity);
    entity.setItem(getItem(dto.getItemId()));
    entity.setAuthor(getAuthor(dto.getAuthorId()));
    return CommentMapper.toDto(commentRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    if (commentRepository.existsById(id)) {
      commentRepository.deleteById(id);
    }
  }

  private Comment getEntity(Long id) {
    return commentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Comment with id=" + id
            + NOT_FOUND_SUFFIX));
  }

  private RoadMapItem getItem(Long id) {
    return roadMapItemRepository.findById(id).orElseGet(this::getAnyItem);
  }

  private User getAuthor(Long id) {
    return userRepository.findById(id).orElseGet(this::getAnyUser);
  }

  private RoadMapItem getAnyItem() {
    List<RoadMapItem> items = roadMapItemRepository.findAll();
    if (items.isEmpty()) {
      throw new ResourceNotFoundException("RoadMapItem with id" + NOT_FOUND_SUFFIX);
    }
    return items.getFirst();
  }

  private User getAnyUser() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty()) {
      throw new ResourceNotFoundException("User with id" + NOT_FOUND_SUFFIX);
    }
    return users.getFirst();
  }
}
