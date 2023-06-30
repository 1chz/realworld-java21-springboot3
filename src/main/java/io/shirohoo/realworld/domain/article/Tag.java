package io.shirohoo.realworld.domain.article;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tag")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, unique = true, nullable = false)
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Tag(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public void addTo(Article article) {
        if (article == null) {
            throw new IllegalArgumentException("article must not be null");
        }

        article.addTag(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Tag other && Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }
}
