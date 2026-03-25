package com.example.roadmap.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public final class PageResponseDto<T> {

  private final List<T> content;
  private final int page;
  private final int size;
  private final long totalElements;
  private final int totalPages;
  private final boolean first;
  private final boolean last;
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
