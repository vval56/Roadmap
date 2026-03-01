package com.example.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CommentDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

  private Long id;

  @NotBlank
  private String content;

  @NotNull
  private Long itemId;

  @NotNull
  private Long authorId;
}
