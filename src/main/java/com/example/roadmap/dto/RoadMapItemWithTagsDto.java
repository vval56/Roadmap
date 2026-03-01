package com.example.roadmap.dto;

import com.example.roadmap.model.ItemStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RoadMapItemWithTagsDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class RoadMapItemWithTagsDto {

  private Long id;
  private String title;
  private ItemStatus status;
  private List<String> tags = new ArrayList<>();
}
