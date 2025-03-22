package io.zhc1.realworld.api.response;

import java.util.List;

public record MultipleCommentsResponse(List<ArticleCommentResponse> comments) {}
