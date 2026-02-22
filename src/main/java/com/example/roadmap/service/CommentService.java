package com.example.roadmap.service;

import com.example.roadmap.model.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();
    Comment getCommentById(Long id);
    List<Comment> getCommentsByItemId(Long itemId);
    Comment createComment(Comment comment);
    Comment updateComment(Long id, Comment comment);
    void deleteComment(Long id);
}