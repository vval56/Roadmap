package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Result payload for transactional bulk operation demonstration")
public class TransactionDemoResultDto {

  @Schema(description = "Roadmap count before the operation", example = "1")
  private long roadMapsBefore;

  @Schema(description = "Roadmap count after the operation", example = "2")
  private long roadMapsAfter;

  @Schema(description = "Roadmap item count before the operation", example = "1")
  private long itemsBefore;

  @Schema(description = "Roadmap item count after the operation", example = "3")
  private long itemsAfter;

  @Schema(description = "Whether the demo was executed inside a single service transaction", example = "true")
  private boolean transactional;

  @Schema(description = "How many bulk items were requested", example = "3")
  private int requestedItems;

  @Schema(description = "Human-readable outcome of the demo", example = "Forced bulk failure after saving 2 items")
  private String message;
}
