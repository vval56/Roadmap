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
@Schema(description = "Roadmap item payload")
public class RoadMapItemDto {

  @Schema(description = "Roadmap item identifier", example = "10")
  private Long id;

  @NotBlank
  @Size(max = 150)
  @Schema(description = "Item title", example = "Learn Spring Data JPA")
  private String title;

  @Size(max = 800)
  @Schema(description = "Detailed item description", example = "Cover repositories, transactions and entity mappings")
  private String details;

  @NotNull
  @Schema(description = "Current item status", example = "IN_PROGRESS")
  private ItemStatus status;

  @NotNull
  @Positive
  @Schema(description = "Roadmap identifier", example = "1")
  private Long roadMapId;

  @Positive
  @Schema(description = "Parent roadmap item identifier", example = "5", nullable = true)
  private Long parentItemId;

  @ArraySchema(schema = @Schema(description = "Tag identifier", example = "2"))
  private Set<@Positive Long> tagIds = new LinkedHashSet<>();
}
