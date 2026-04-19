package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kind of async task being processed")
public enum AsyncTaskType {
  ROADMAP_ANALYTICS_REPORT,
  ROADMAP_ITEM_BULK_CREATE
}
