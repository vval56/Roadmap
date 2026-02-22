package com.example.roadmap.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // например: "Java", "Urgent"

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<RoadMapItem> items = new ArrayList<>();

    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<RoadMapItem> getItems() { return items; }
    public void setItems(List<RoadMapItem> items) { this.items = items; }
}