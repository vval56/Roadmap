package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current async task state")
public enum AsyncTaskStatus {
  PENDING,
  RUNNING,
  COMPLETED,
  FAILED
}
