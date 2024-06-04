package io.zhc1.realworld.api.request;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.zhc1.realworld.core.model.Tag;

public record WriteArticleRequest(Params article) {
    public Set<Tag> tags() {
        return article.tagList.stream().map(Tag::new).collect(Collectors.toSet());
    }

    public record Params(String title, String description, String body, List<String> tagList) {}
}
