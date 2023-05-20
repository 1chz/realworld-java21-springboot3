package io.github.shirohoo.realworld.domain.article;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @Column(unique = true)
    private String slug;

    @Column(unique = true)
    private String title;

    private String description;

    private String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_favorites",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> favorites = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private Article(
            Integer id,
            User author,
            String title,
            String description,
            String content,
            Set<User> favorites,
            Set<Tag> tags,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.author = author;
        this.slug = createSlugBy(title);
        this.title = title;
        this.description = description;
        this.content = content;
        this.favorites = favorites == null ? new HashSet<>() : favorites;
        this.tags = tags == null ? new HashSet<>() : tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Article update(User author, String title, String description, String content) {
        if (!this.isWritten(author)) {
            throw new IllegalArgumentException("You can't edit articles written by others.");
        }

        if (title != null && !title.isBlank()) {
            this.title(title);
        }

        if (description != null && !description.isBlank()) {
            this.description = description;
        }

        if (content != null && !content.isBlank()) {
            this.content = content;
        }

        return this;
    }

    public Article title(String title) {
        this.slug = createSlugBy(title);
        this.title = title;
        return this;
    }

    public Article addTag(Tag tag) {
        if (this.tags.contains(tag)) {
            return this;
        }

        this.tags.add(tag);
        tag.tag(this);

        return this;
    }

    public Article favorite(User user) {
        if (this.favorites.contains(user)) {
            return this;
        }

        this.favorites.add(user);
        user.favorite(this);

        return this;
    }

    public Article unfavorite(User user) {
        if (!this.favorites.contains(user)) {
            return this;
        }

        this.favorites.remove(user);
        user.unfavorite(this);

        return this;
    }

    public boolean isFavoriteBy(User user) {
        return this.favorites.contains(user);
    }

    public boolean isWritten(User user) {
        return this.author.equals(user);
    }

    public boolean isTaggedBy(Tag tag) {
        return this.tags.contains(tag);
    }

    public int favoriteCount() {
        return this.favorites.size();
    }

    public String[] tags() {
        return this.tags.stream().map(Tag::getName).sorted().toArray(String[]::new);
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
