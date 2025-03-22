package io.zhc1.realworld.api.request;

public record WriteCommentRequest(Params comment) {
    public record Params(String body) {}
}
