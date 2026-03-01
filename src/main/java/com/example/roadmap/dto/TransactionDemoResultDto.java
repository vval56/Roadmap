package com.example.roadmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TransactionDemoResultDto component.
 */
@Getter
@Setter
@NoArgsConstructor
public class TransactionDemoResultDto {

  private long roadMapsBefore;
  private long roadMapsAfter;
  private long itemsBefore;
  private long itemsAfter;
  private String message;
}
