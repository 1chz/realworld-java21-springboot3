package io.zhc1.realworld.api.request;

public record EditArticleRequest(Params article) {
    public record Params(String title, String description, String body) {}
}
