package io.shirohoo.realworld.application.article.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.domain.article.Tag;
import io.shirohoo.realworld.domain.article.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<String> getTags() {
        return tagRepository.findAll().stream().map(Tag::getName).toList();
    }
}
