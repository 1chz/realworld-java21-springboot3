package sample.shirohoo.realworld.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.core.model.Tag;
import sample.shirohoo.realworld.core.model.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    /**
     * Get all tags.
     * @return Returns all tags
     */
    public List<Tag> getTags() {
        // Note: If there are too many tags, you can apply pagination.
        return tagRepository.findAll();
    }
}
