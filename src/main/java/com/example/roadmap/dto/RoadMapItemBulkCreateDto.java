package com.example.roadmap.dto;

import com.example.roadmap.model.ItemStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Single roadmap item payload used in bulk creation requests")
public class RoadMapItemBulkCreateDto {

  @NotBlank
  @Size(max = 150)
  @Schema(description = "Item title", example = "Configure PostgreSQL")
  private String title;

  @Size(max = 800)
  @Schema(description = "Detailed item description", example = "Prepare docker compose and datasource settings")
  private String details;

  @NotNull
  @Schema(description = "Current item status", example = "PLANNED")
  private ItemStatus status;

  @Positive
  @Schema(description = "Existing parent roadmap item identifier", example = "5", nullable = true)
  private Long parentItemId;

  @ArraySchema(schema = @Schema(description = "Tag identifier", example = "2"))
  private Set<@Positive Long> tagIds = new LinkedHashSet<>();
}
