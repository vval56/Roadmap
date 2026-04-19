package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Result of asynchronous roadmap item bulk creation")
public class AsyncRoadMapItemBulkResultDto {

  @Schema(description = "Roadmap identifier that received created items", example = "2")
  private Long roadMapId;

  @Schema(description = "How many roadmap items were created", example = "2")
  private int createdItemsCount;

  @ArraySchema(arraySchema = @Schema(description = "Identifiers of created roadmap items"))
  private List<Long> createdItemIds;

  @Schema(description = "Timestamp when bulk creation finished", example = "2026-04-08T12:40:05+03:00")
  private OffsetDateTime finishedAt;
}
