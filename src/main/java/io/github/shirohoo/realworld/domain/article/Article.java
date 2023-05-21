package io.github.shirohoo.realworld.domain.article;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @Column(name = "article_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(length = 50, nullable = false)
    private String description;

    @Column(length = 50, unique = true, nullable = false)
    private String title;

    @Column(length = 50, unique = true, nullable = false)
    private String slug;

    @Column(length = 1_000, nullable = false)
    private String content = "";

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleFavorite> favoritedUsers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArticleTag> tags = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private Article(Integer id, User author, String description, String title, String content) {
        this.id = id;
        this.author = author;
        this.description = description;
        this.title = title;
        this.slug = createSlugBy(title);
        this.content = content;
        this.favoritedUsers = new HashSet<>();
        this.tags = new HashSet<>();
        this.createdAt = LocalDateTime.now();
    }

    public Article update(User author, String title, String description, String content) {
        if (!this.isWritten(author)) {
            throw new IllegalArgumentException("You can't edit articles written by others.");
        }

        if (title != null && !title.isBlank()) {
            this.slug = createSlugBy(title);
            this.title = title;
        }

        if (description != null && !description.isBlank()) {
            this.description = description;
        }

        if (content != null && !content.isBlank()) {
            this.content = content;
        }

        return this;
    }

    public ArticleVO fetchDetailBy(User me) {
        return new ArticleVO(me, this);
    }

    public boolean isWritten(User user) {
        return this.author.equals(user);
    }

    public int numberOfLikes() {
        return this.favoritedUsers.size();
    }

    public List<Tag> tags() {
        return this.tags.stream().map(ArticleTag::getTag).toList();
    }

    public String[] getTagNames() {
        return this.tags.stream()
                .map(ArticleTag::getTag)
                .map(Tag::getName)
                .sorted()
                .toArray(String[]::new);
    }

    private String createSlugBy(String title) {
        return title.toLowerCase().replaceAll("\\s+", "-");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Article other && Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
