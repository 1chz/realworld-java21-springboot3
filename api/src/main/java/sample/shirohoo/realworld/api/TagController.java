package sample.shirohoo.realworld.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import sample.shirohoo.realworld.api.response.TagsResponse;
import sample.shirohoo.realworld.core.service.TagService;

@RestController
@RequiredArgsConstructor
class TagController {
    private final TagService tagService;

    @GetMapping("/api/tags")
    TagsResponse doGet() {
        return new TagsResponse(tagService.getAllTags());
    }
}
