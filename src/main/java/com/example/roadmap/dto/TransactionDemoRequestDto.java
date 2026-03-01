package com.example.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * TransactionDemoRequestDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class TransactionDemoRequestDto {

  @NotNull
  private Long ownerId;

  @NotBlank
  private String roadMapTitle;

  @NotBlank
  private String firstItemTitle;

  @NotBlank
  private String secondItemTitle;
}
