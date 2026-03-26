package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Tag payload")
public class TagDto {

  @Schema(description = "Tag identifier", example = "2")
  private Long id;

  @NotBlank
  @Size(max = 80)
  @Schema(description = "Unique tag name", example = "spring")
  private String name;
}
