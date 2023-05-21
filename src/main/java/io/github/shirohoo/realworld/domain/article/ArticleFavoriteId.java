package io.github.shirohoo.realworld.domain.article;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleFavoriteId implements Serializable {
    private UUID userId;
    private Integer articleId;
}
