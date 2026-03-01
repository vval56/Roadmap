package com.example.roadmap.repository;

import com.example.roadmap.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for comments.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
