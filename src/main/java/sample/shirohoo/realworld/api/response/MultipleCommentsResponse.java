package sample.shirohoo.realworld.api.response;

import java.util.List;

public record MultipleCommentsResponse(List<ArticleCommentResponse> comments) {}
