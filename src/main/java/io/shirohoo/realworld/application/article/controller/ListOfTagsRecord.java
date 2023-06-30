package io.shirohoo.realworld.application.article.controller;

import java.util.List;

public record ListOfTagsRecord(String[] tags) {
    public ListOfTagsRecord(List<String> tags) {
        this(tags.toArray(String[]::new));
    }
}
