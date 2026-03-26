package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Comment payload")
public class CommentDto {

  @Schema(description = "Comment identifier", example = "4")
  private Long id;

  @NotBlank
  @Size(max = 600)
  @Schema(description = "Comment content", example = "Focus on transactions before moving to native queries")
  private String content;

  @NotNull
  @Positive
  @Schema(description = "Roadmap item identifier", example = "10")
  private Long itemId;

  @NotNull
  @Positive
  @Schema(description = "Author user identifier", example = "1")
  private Long authorId;
}
