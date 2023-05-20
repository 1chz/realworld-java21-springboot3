package io.github.shirohoo.realworld.application.article.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("comment")
public record CreateCommentRequest(String body) {}
