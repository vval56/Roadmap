package com.example.roadmap.controller;

import com.example.roadmap.dto.TagDTO;
import com.example.roadmap.dto.TagMapper;
import com.example.roadmap.model.Tag;
import com.example.roadmap.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<Tag> tags = service.getAllTags();
        List<TagDTO> dtos = TagMapper.toDTOList(tags);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Long id) {
        Tag tag = service.getTagById(id);
        return tag != null ? ResponseEntity.ok(TagMapper.toDTO(tag)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TagDTO> getTagByName(@PathVariable String name) {
        Tag tag = service.getTagByName(name);
        return tag != null ? ResponseEntity.ok(TagMapper.toDTO(tag)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TagDTO>> searchTagsByName(@RequestParam String name) {
        List<Tag> tags = service.searchTagsByName(name);
        List<TagDTO> dtos = TagMapper.toDTOList(tags);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<TagDTO> createTag(@RequestBody TagDTO dto) {
        Tag entity = TagMapper.toEntity(dto);
        Tag created = service.createTag(entity);
        return ResponseEntity.ok(TagMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(@PathVariable Long id, @RequestBody TagDTO dto) {
        Tag entity = TagMapper.toEntity(dto);
        Tag updated = service.updateTag(id, entity);
        return updated != null ? ResponseEntity.ok(TagMapper.toDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        service.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}