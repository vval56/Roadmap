package com.example.roadmap.controller;

import com.example.roadmap.dto.CommentDTO;
import com.example.roadmap.dto.CommentMapper;
import com.example.roadmap.model.Comment;
import com.example.roadmap.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        List<Comment> comments = service.getAllComments();
        List<CommentDTO> dtos = CommentMapper.toDTOList(comments);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        Comment comment = service.getCommentById(id);
        return comment != null ? ResponseEntity.ok(CommentMapper.toDTO(comment)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByItemId(@PathVariable Long itemId) {
        List<Comment> comments = service.getCommentsByItemId(itemId);
        List<CommentDTO> dtos = CommentMapper.toDTOList(comments);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO dto) {
        Comment entity = CommentMapper.toEntity(dto);
        Comment created = service.createComment(entity);
        return ResponseEntity.ok(CommentMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        Comment entity = CommentMapper.toEntity(dto);
        Comment updated = service.updateComment(id, entity);
        return updated != null ? ResponseEntity.ok(CommentMapper.toDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}