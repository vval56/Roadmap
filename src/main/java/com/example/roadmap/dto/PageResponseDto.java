package com.example.roadmap.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "Standard paginated response")
public final class PageResponseDto<T> {

  @ArraySchema(schema = @Schema(description = "Page content"))
  private final List<T> content;
  @Schema(description = "Current page number", example = "0")
  private final int page;
  @Schema(description = "Page size", example = "5")
  private final int size;
  @Schema(description = "Total number of elements", example = "12")
  private final long totalElements;
  @Schema(description = "Total number of pages", example = "3")
  private final int totalPages;
  @Schema(description = "Whether this is the first page", example = "true")
  private final boolean first;
  @Schema(description = "Whether this is the last page", example = "false")
  private final boolean last;
  @Schema(description = "Whether this page is empty", example = "false")
  private final boolean empty;

  private PageResponseDto(Page<T> pageData) {
    this.content = pageData.getContent();
    this.page = pageData.getNumber();
    this.size = pageData.getSize();
    this.totalElements = pageData.getTotalElements();
    this.totalPages = pageData.getTotalPages();
    this.first = pageData.isFirst();
    this.last = pageData.isLast();
    this.empty = pageData.isEmpty();
  }

  public static <T> PageResponseDto<T> from(Page<T> pageData) {
    return new PageResponseDto<>(pageData);
  }

  public List<T> getContent() {
    return content;
  }

  public int getPage() {
    return page;
  }

  public int getSize() {
    return size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public boolean isFirst() {
    return first;
  }

  public boolean isLast() {
    return last;
  }

  public boolean isEmpty() {
    return empty;
  }
}
