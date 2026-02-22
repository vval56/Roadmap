package com.example.roadmap.controller;

import com.example.roadmap.dto.RoadMapDTO;
import com.example.roadmap.dto.RoadMapMapper;
import com.example.roadmap.model.RoadMap;
import com.example.roadmap.service.RoadMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadmaps")
public class RoadMapController {

    private final RoadMapService service;

    public RoadMapController(RoadMapService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RoadMapDTO>> getAllRoadMaps() {
        List<RoadMap> roadMaps = service.getAllRoadMaps();
        List<RoadMapDTO> dtos = RoadMapMapper.toDTOList(roadMaps);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadMapDTO> getRoadMapById(@PathVariable Long id) {
        RoadMap roadMap = service.getRoadMapById(id);
        return roadMap != null ? ResponseEntity.ok(RoadMapMapper.toDTO(roadMap)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoadMapDTO>> getRoadMapsByUserId(@PathVariable Long userId) {
        List<RoadMap> roadMaps = service.getRoadMapsByUserId(userId);
        List<RoadMapDTO> dtos = RoadMapMapper.toDTOList(roadMaps);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<RoadMapDTO> createRoadMap(@RequestBody RoadMapDTO dto) {
        RoadMap entity = RoadMapMapper.toEntity(dto);
        RoadMap created = service.createRoadMap(entity);
        return ResponseEntity.ok(RoadMapMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoadMapDTO> updateRoadMap(@PathVariable Long id, @RequestBody RoadMapDTO dto) {
        RoadMap entity = RoadMapMapper.toEntity(dto);
        RoadMap updated = service.updateRoadMap(id, entity);
        return updated != null ? ResponseEntity.ok(RoadMapMapper.toDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoadMap(@PathVariable Long id) {
        service.deleteRoadMap(id);
        return ResponseEntity.noContent().build();
    }
}