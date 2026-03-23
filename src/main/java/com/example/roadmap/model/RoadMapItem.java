package com.example.roadmap.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "roadmap_items")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"roadMap", "comments", "tags", "parentItem", "childItems"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoadMapItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(length = 800)
  private String details;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ItemStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "roadmap_id", nullable = false)
  private RoadMap roadMap;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_item_id")
  private RoadMapItem parentItem;

  @OneToMany(mappedBy = "parentItem", fetch = FetchType.LAZY)
  private Set<RoadMapItem> childItems = new LinkedHashSet<>();

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Comment> comments = new LinkedHashSet<>();

  @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "roadmap_item_tag",
      joinColumns = @JoinColumn(name = "roadmap_item_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new LinkedHashSet<>();
}
