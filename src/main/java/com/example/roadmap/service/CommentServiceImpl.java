package com.example.roadmap.service;

import com.example.roadmap.model.Comment;
import com.example.roadmap.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    public CommentServiceImpl(CommentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByItemId(Long itemId) {
        return repository.findByRoadMapItemId(itemId);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        return repository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, Comment comment) {
        Comment existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setText(comment.getText());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        repository.deleteById(id);
    }
}