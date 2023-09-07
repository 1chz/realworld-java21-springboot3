package io.shirohoo.realworld.application.article.controller;

import static java.util.stream.Collectors.toSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonRootName;

import io.shirohoo.realworld.domain.article.Tag;

@JsonRootName("article")
public record CreateArticleRequest(String title, String description, String body, List<String> tagList) {

    public Set<Tag> tags() {
        return tagList == null ?
            new HashSet<>() :
            tagList
                .stream()
                .map(Tag::new)
                .collect(toSet());
    }

}
