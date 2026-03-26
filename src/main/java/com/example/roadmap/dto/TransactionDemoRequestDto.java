package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

  @NotBlank
  @Size(max = 150)
  @Schema(description = "First item title", example = "Step 1")
  private String firstItemTitle;

  @NotBlank
  @Size(max = 150)
  @Schema(description = "Second item title", example = "Step 2")
  private String secondItemTitle;
}
