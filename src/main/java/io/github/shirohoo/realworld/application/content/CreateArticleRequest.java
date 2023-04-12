package io.github.shirohoo.realworld.application.content;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("article")
public record CreateArticleRequest(String title, String description, String body, String[] tagList) {}
