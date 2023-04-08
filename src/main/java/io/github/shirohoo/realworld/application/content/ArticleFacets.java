package io.github.shirohoo.realworld.application.content;

record ArticleFacets(String tag, String author, String favorited, int offset, int limit) {
    ArticleFacets {
        if (offset < 0) {
            offset = 0;
        }
        if (limit < 0 || limit > 100) {
            limit = 20;
        }
    }
}
