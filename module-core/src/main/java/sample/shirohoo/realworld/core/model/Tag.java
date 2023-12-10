package sample.shirohoo.realworld.core.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 20, unique = true, nullable = false)
  private String name;

  @Column(nullable = false, updatable = false)
  private final LocalDateTime createdAt = LocalDateTime.now();

  public Tag(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name is null or blank.");
    }

    this.name = name;
  }

  public boolean equalsArticleTag(ArticleTag articleTag) {
    return this.equals(articleTag.getTag());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Tag other
        && (Objects.equals(this.getId(), other.getId())
            || Objects.equals(this.getName(), other.getName()));
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getName());
  }
}
