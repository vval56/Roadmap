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

  public RoadMapItemSearchKey(String queryType, String ownerEmail, String roadMapTitle,
                              String parentTitle, String tagName,
                              String status, int page, int size, String sort) {
    this.queryType = normalize(queryType);
    this.ownerEmail = normalize(ownerEmail);
    this.roadMapTitle = normalize(roadMapTitle);
    this.parentTitle = normalize(parentTitle);
    this.tagName = normalize(tagName);
    this.status = normalize(status);
    this.page = page;
    this.size = size;
    this.sort = normalize(sort);
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

  @Override
  public String toString() {
    return "RoadMapItemSearchKey{"
        + "queryType='" + queryType + '\''
        + ", ownerEmail='" + ownerEmail + '\''
        + ", roadMapTitle='" + roadMapTitle + '\''
        + ", parentTitle='" + parentTitle + '\''
        + ", tagName='" + tagName + '\''
        + ", status='" + status + '\''
        + ", page=" + page
        + ", size=" + size
        + ", sort='" + sort + '\''
        + '}';
  }
}
