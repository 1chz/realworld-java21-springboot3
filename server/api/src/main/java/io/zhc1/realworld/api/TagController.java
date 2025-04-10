package io.zhc1.realworld.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import io.zhc1.realworld.api.response.TagsResponse;
import io.zhc1.realworld.service.TagService;

@RestController
@RequiredArgsConstructor
class TagController {
    private final TagService tagService;

    @GetMapping("/api/tags")
    TagsResponse getAllTags() {
        return new TagsResponse(tagService.getAllTags());
    }
}
