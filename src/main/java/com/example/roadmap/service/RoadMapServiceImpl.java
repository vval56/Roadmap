package com.example.roadmap.service;

import com.example.roadmap.model.RoadMap;
import com.example.roadmap.repository.RoadMapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoadMapServiceImpl implements RoadMapService {

    private final RoadMapRepository repository;

    public RoadMapServiceImpl(RoadMapRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMap> getAllRoadMaps() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RoadMap getRoadMapById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadMap> getRoadMapsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public RoadMap createRoadMap(RoadMap roadMap) {
        return repository.save(roadMap);
    }

    @Override
    @Transactional
    public RoadMap updateRoadMap(Long id, RoadMap roadMap) {
        RoadMap existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setTitle(roadMap.getTitle());
            existing.setDescription(roadMap.getDescription());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteRoadMap(Long id) {
        repository.deleteById(id);
    }
}