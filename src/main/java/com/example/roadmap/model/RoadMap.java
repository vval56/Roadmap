package com.example.roadmap.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roadmaps")
public class RoadMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    // === СВЯЗЬ С USER (ManyToOne) ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // === СВЯЗЬ С ROADMAPITEM (OneToMany) ===
    @OneToMany(mappedBy = "roadMap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoadMapItem> items = new ArrayList<>();

    public RoadMap() {}

    // === ГЕТТЕРЫ И СЕТТЕРЫ ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // === ВАЖНО: Геттер и сеттер для user ===
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<RoadMapItem> getItems() { return items; }
    public void setItems(List<RoadMapItem> items) { this.items = items; }
}