package io.github.shirohoo.realworld.domain.content;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true, chain = true)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Article> articles = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }

    public void addTag(Article article) {
        if (this.articles.contains(article)) return;
        this.articles.add(article);
        article.addTag(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tag other) return Objects.equals(this.name, other.name);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
