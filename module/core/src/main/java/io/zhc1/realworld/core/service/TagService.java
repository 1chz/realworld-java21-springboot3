package io.zhc1.realworld.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.core.model.Tag;
import io.zhc1.realworld.core.model.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /**
     * Get all tags.
     *
     * @return Returns all tags
     */
    public List<Tag> getAllTags() {
        // Note: If there are too many tags, recommend apply cursor based pagination.
        return tagRepository.findAll();
    }
}
