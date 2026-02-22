package com.example.roadmap.dto;

import com.example.roadmap.model.Tag;
import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {
    public static TagDTO toDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public static Tag toEntity(TagDTO dto) {
        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        return tag;
    }

    public static List<TagDTO> toDTOList(List<Tag> tags) {
        return tags.stream().map(TagMapper::toDTO).collect(Collectors.toList());
    }
}