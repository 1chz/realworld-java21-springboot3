package sample.shirohoo.realworld.core.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(length = 500, nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ArticleComment(Article article, User author, String content) {
        if (article == null || article.getId() == null) {
            throw new IllegalArgumentException("article is null or not saved article.");
        }
        if (author == null || author.getId() == null) {
            throw new IllegalArgumentException("author is null or unknown user.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be null or blank.");
        }

        this.article = article;
        this.author = author;
        this.content = content;
    }

    public boolean isNotAuthor(User user) {
        return !this.author.equals(user);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ArticleComment other && Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
