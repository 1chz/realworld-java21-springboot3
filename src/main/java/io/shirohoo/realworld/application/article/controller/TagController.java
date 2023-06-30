package io.shirohoo.realworld.application.article.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.shirohoo.realworld.application.article.service.TagService;

@RestController
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/api/tags")
    public ListOfTagsRecord getTags() {
        List<String> tags = tagService.getTags();
        return new ListOfTagsRecord(tags);
    }
}
