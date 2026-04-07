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
@Schema(description = "Generated analytics report for one roadmap")
public class RoadMapAnalyticsReportDto {

  @Schema(description = "Roadmap identifier", example = "2")
  private Long roadMapId;

  @Schema(description = "Roadmap title", example = "Java Backend Roadmap")
  private String roadMapTitle;

  @Schema(description = "Owner email", example = "vladislav@example.com")
  private String ownerEmail;

  @Schema(description = "Total number of roadmap items", example = "5")
  private int totalItems;

  @Schema(description = "Planned item count", example = "2")
  private int plannedItems;

  @Schema(description = "Items in progress", example = "2")
  private int inProgressItems;

  @Schema(description = "Done item count", example = "1")
  private int doneItems;

  @Schema(description = "Total comments across all roadmap items", example = "4")
  private int totalComments;

  @Schema(description = "Completion percentage", example = "20.0")
  private double completionRatePercent;

  @ArraySchema(arraySchema = @Schema(description = "Distinct tag names found in the roadmap"))
  private List<String> distinctTagNames;

  @Schema(description = "Timestamp when the report was generated", example = "2026-04-07T12:00:00+03:00")
  private OffsetDateTime generatedAt;
}
