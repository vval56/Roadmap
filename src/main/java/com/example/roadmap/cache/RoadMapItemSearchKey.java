package com.example.roadmap.cache;

import java.util.Objects;

public final class RoadMapItemSearchKey {

  private final String queryType;
  private final String ownerEmail;
  private final String roadMapTitle;
  private final String parentTitle;
  private final String tagName;
  private final String status;
  private final int page;
  private final int size;
  private final String sort;

  private RoadMapItemSearchKey(SearchCriteria criteria, PageDescriptor pageDescriptor) {
    this.queryType = criteria.queryType;
    this.ownerEmail = criteria.ownerEmail;
    this.roadMapTitle = criteria.roadMapTitle;
    this.parentTitle = criteria.parentTitle;
    this.tagName = criteria.tagName;
    this.status = criteria.status;
    this.page = pageDescriptor.page;
    this.size = pageDescriptor.size;
    this.sort = pageDescriptor.sort;
  }

  public static RoadMapItemSearchKey of(String queryType, String ownerEmail, String roadMapTitle,
                                        String parentTitle, String tagName, String status,
                                        int page, int size, String sort) {
    return new RoadMapItemSearchKey(
        new SearchCriteria(queryType, ownerEmail, roadMapTitle, parentTitle, tagName, status),
        new PageDescriptor(page, size, sort)
    );
  }

  private static String normalize(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim().toLowerCase();
    return normalized.isEmpty() ? null : normalized;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof RoadMapItemSearchKey that)) {
      return false;
    }
    return page == that.page
        && size == that.size
        && Objects.equals(queryType, that.queryType)
        && Objects.equals(ownerEmail, that.ownerEmail)
        && Objects.equals(roadMapTitle, that.roadMapTitle)
        && Objects.equals(parentTitle, that.parentTitle)
        && Objects.equals(tagName, that.tagName)
        && Objects.equals(status, that.status)
        && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryType, ownerEmail, roadMapTitle, parentTitle,
        tagName, status, page, size, sort);
  }

  private static final class SearchCriteria {
    private final String queryType;
    private final String ownerEmail;
    private final String roadMapTitle;
    private final String parentTitle;
    private final String tagName;
    private final String status;

    private SearchCriteria(String queryType, String ownerEmail, String roadMapTitle,
                           String parentTitle, String tagName, String status) {
      this.queryType = normalize(queryType);
      this.ownerEmail = normalize(ownerEmail);
      this.roadMapTitle = normalize(roadMapTitle);
      this.parentTitle = normalize(parentTitle);
      this.tagName = normalize(tagName);
      this.status = normalize(status);
    }
  }

  private static final class PageDescriptor {
    private final int page;
    private final int size;
    private final String sort;

    private PageDescriptor(int page, int size, String sort) {
      this.page = page;
      this.size = size;
      this.sort = normalize(sort);
    }
  }
}
