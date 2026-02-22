package com.example.roadmap.dto;

import com.example.roadmap.model.Comment;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {
    public static CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getRoadMapItem() != null) {
            dto.setRoadMapItemId(comment.getRoadMapItem().getId());
        }
        return dto;
    }

    public static Comment toEntity(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setCreatedAt(dto.getCreatedAt());
        return comment;
    }

    public static List<CommentDTO> toDTOList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDTO).collect(Collectors.toList());
    }
}