package io.github.shirohoo.realworld.domain.content;

import io.github.shirohoo.realworld.domain.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
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

    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "article_favorites",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> favorites = new HashSet<>();

    @Builder.Default
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Article addTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Article favoritedBy(User user) {
        if (this.favorites.contains(user)) return this;
        this.favorites.add(user);
        user.favorite(this);
        return this;
    }

    public Article unfavoritedBy(User user) {
        if (!this.favorites.contains(user)) return this;
        this.favorites.remove(user);
        user.unfavorite(this);
        return this;
    }

    public String[] tagList() {
        return this.tags.stream().map(Tag::name).sorted().toArray(String[]::new);
    }

    public boolean hasFavorited(User user) {
        return this.favorites.contains(user);
    }

    public int favoritesCount() {
        return this.favorites.size();
    }

    public boolean isAuthoredBy(User user) {
        return this.author.equals(user);
    }

    public Set<User> favorites() {
        return Set.copyOf(this.favorites);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Article other) return this.id.equals(other.id);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
