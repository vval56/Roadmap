package com.example.roadmap.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * Data transfer object for RoadMapItem API.
 */
public class RoadMapItemDto {

  private Long id;

  @NotBlank
  private String title;

  private String description;

  @NotBlank
  private String status;

  private LocalDate targetDate;

  public RoadMapItemDto() {
  }

  /**
   * Creates DTO with all fields.
   *
   * @param id item id
   * @param title item title
   * @param description item description
   * @param status item status
   * @param targetDate target completion date
   */
  public RoadMapItemDto(Long id, String title, String description, String status,
      LocalDate targetDate) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.status = status;
    this.targetDate = targetDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getTargetDate() {
    return targetDate;
  }

  public void setTargetDate(LocalDate targetDate) {
    this.targetDate = targetDate;
  }
}
