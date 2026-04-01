package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Single roadmap item payload used in bulk creation requests")
public class RoadMapItemBulkCreateDto extends BaseRoadMapItemRequestDto {
}
