package com.example.roadmap.service;

import com.example.roadmap.model.Tag;
import com.example.roadmap.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository repository;

    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return repository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> searchTagsByName(String name) {
        return repository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        return repository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(Long id, Tag tag) {
        Tag existing = repository.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(tag.getName());
            return repository.save(existing);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        repository.deleteById(id);
    }
}