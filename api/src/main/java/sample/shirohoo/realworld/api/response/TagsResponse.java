package sample.shirohoo.realworld.api.response;

import java.util.Collection;

import sample.shirohoo.realworld.core.model.Tag;

public record TagsResponse(String[] tags) {
    public TagsResponse(Collection<Tag> tags) {
        this(tags.stream().map(Tag::getName).toArray(String[]::new));
    }
}
