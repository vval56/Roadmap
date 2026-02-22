package com.example.roadmap.service;

import com.example.roadmap.model.RoadMap;
import java.util.List;

public interface RoadMapService {
    List<RoadMap> getAllRoadMaps();
    RoadMap getRoadMapById(Long id);
    List<RoadMap> getRoadMapsByUserId(Long userId);
    RoadMap createRoadMap(RoadMap roadMap);
    RoadMap updateRoadMap(Long id, RoadMap roadMap);
    void deleteRoadMap(Long id);
}