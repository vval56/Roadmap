package com.example.roadmap.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"roadMaps", "comments"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Column(length = 120)
  private String firstName;

  @Column(length = 120)
  private String lastName;

  @Column(name = "full_name", nullable = false, length = 241)
  private String fullName;

  @Column(nullable = false, unique = true, length = 160)
  private String email;

  @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
  private Set<RoadMap> roadMaps = new LinkedHashSet<>();

  @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
  private Set<Comment> comments = new LinkedHashSet<>();

  @PrePersist
  @PreUpdate
  void syncFullName() {
    String normalizedFirstName = normalizeNamePart(firstName);
    String normalizedLastName = normalizeNamePart(lastName);
    fullName = (normalizedFirstName + " " + normalizedLastName).trim();
  }

  private String normalizeNamePart(String value) {
    return value == null ? "" : value.trim();
  }
}
