package sample.shirohoo.realworld.api.request;

import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;

import sample.shirohoo.realworld.core.model.Tag;

public record WriteArticleRequest(Params article) {
    public Set<Tag> tags() {
        return article.tagList.stream().map(Tag::new).collect(toSet());
    }

    public record Params(String title, String description, String body, List<String> tagList) {}
}
