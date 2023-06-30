package io.shirohoo.realworld.domain.article;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleTagId implements Serializable {
    private Integer articleId;
    private Integer tagId;
}
