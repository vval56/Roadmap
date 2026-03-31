package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request payload for transaction demonstration")
public class TransactionDemoRequestDto {

  @NotNull
  @Positive
  @Schema(description = "Owner identifier", example = "1")
  private Long ownerId;

  @NotBlank
  @Size(max = 120)
  @Schema(description = "Roadmap title to create", example = "Transaction Demo")
  private String roadMapTitle;

  @Valid
  @NotEmpty
  @Size(min = 2, max = 20)
  @Schema(description = "Roadmap items that participate in the bulk transaction demo")
  private List<@Valid RoadMapItemBulkCreateDto> items = new ArrayList<>();
}
