package sample.shirohoo.realworld.api.request;

public record WriteCommentRequest(Params comment) {
    public record Params(String body) {}
}
