package com.example.roadmap.service;

import com.example.roadmap.model.Tag;
import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(Long id);
    Tag getTagByName(String name);
    List<Tag> searchTagsByName(String name);
    Tag createTag(Tag tag);
    Tag updateTag(Long id, Tag tag);
    void deleteTag(Long id);
}