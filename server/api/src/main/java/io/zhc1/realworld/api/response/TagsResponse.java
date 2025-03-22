package io.zhc1.realworld.api.response;

import java.util.Collection;

import io.zhc1.realworld.core.model.Tag;

public record TagsResponse(String[] tags) {
    public TagsResponse(Collection<Tag> tags) {
        this(tags.stream().map(Tag::getName).toArray(String[]::new));
    }
}
