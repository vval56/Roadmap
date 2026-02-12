package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMapItem;

import java.util.ArrayList;
import java.util.List;

public class RoadMapItemMapper {
    public static RoadMapItemDTO toDTO(RoadMapItem entity) {
        if (entity == null) {
            return null;
        }

        RoadMapItemDTO dto = new RoadMapItemDTO();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setDescription(entity.getDescription());
        dto.setTitle(entity.getTitle());
        dto.setStartDate(entity.getStartDate());
        dto.setTargetDate(entity.getTargetDate());

        return dto;
    }

    public static RoadMapItem toEntity(RoadMapItemDTO dto) {
        if (dto == null) {
            return null;
        }

        RoadMapItem entity = new RoadMapItem();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setPriority(dto.getPriority());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setStartDate(dto.getStartDate());
        entity.setTargetDate(dto.getTargetDate());

        return entity;
    }

    public static List<RoadMapItemDTO> toDTOList(List<RoadMapItem> entities) {
        if (entities == null) {
            return null;
        }

        List<RoadMapItemDTO> dtos = new ArrayList<>();
        for (RoadMapItem entity : entities) {
            dtos.add(toDTO(entity));
        }

        return dtos;
    }

    public static List<RoadMapItem> toEntityList(List<RoadMapItemDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        List<RoadMapItem> entities = new ArrayList<>();
        for (RoadMapItemDTO dto : dtos) {
            entities.add(toEntity(dto));
        }

        return entities;
    }
}
