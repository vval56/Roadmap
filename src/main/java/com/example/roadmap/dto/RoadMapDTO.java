package com.example.roadmap.dto;

import lombok.Data;

@Data
public class RoadMapDTO {
    private Long id;
    private String title;
    private String description;
    private Long userId;
}