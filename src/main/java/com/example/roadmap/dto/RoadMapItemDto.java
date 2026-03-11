package com.example.roadmap.dto;

import com.example.roadmap.model.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoadMapItemDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class RoadMapItemDto {

  private Long id;

  @NotBlank
  private String title;

  private String details;

  @NotNull
  private ItemStatus status;

  @NotNull
  private Long roadMapId;

  private Long parentItemId;

  private Set<Long> tagIds = new LinkedHashSet<>();
}
