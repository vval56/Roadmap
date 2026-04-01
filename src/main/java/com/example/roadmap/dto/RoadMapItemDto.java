package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Roadmap item payload")
public class RoadMapItemDto extends BaseRoadMapItemRequestDto {

  @Schema(description = "Roadmap item identifier", example = "10")
  private Long id;

  @NotNull
  @Positive
  @Schema(description = "Roadmap identifier", example = "1")
  private Long roadMapId;
}
