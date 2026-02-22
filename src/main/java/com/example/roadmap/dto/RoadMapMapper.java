package com.example.roadmap.dto;

import com.example.roadmap.model.RoadMap;
import java.util.List;
import java.util.stream.Collectors;

public class RoadMapMapper {
    public static RoadMapDTO toDTO(RoadMap roadMap) {
        RoadMapDTO dto = new RoadMapDTO();
        dto.setId(roadMap.getId());
        dto.setTitle(roadMap.getTitle());
        dto.setDescription(roadMap.getDescription());
        if (roadMap.getUser() != null) {
            dto.setUserId(roadMap.getUser().getId());
        }
        return dto;
    }

    public static RoadMap toEntity(RoadMapDTO dto) {
        RoadMap roadMap = new RoadMap();
        roadMap.setId(dto.getId());
        roadMap.setTitle(dto.getTitle());
        roadMap.setDescription(dto.getDescription());
        return roadMap;
    }

    public static List<RoadMapDTO> toDTOList(List<RoadMap> roadMaps) {
        return roadMaps.stream().map(RoadMapMapper::toDTO).collect(Collectors.toList());
    }
}