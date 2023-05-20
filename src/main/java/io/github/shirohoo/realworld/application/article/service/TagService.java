package io.github.shirohoo.realworld.application.article.service;

import io.github.shirohoo.realworld.domain.article.Tag;
import io.github.shirohoo.realworld.domain.article.TagRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<String> getTags() {
        return tagRepository.findAll().stream().map(Tag::getName).toList();
    }
}
