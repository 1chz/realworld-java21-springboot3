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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "user_follow",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_id", "following_id"})})
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    public UserFollow(User follower, User following) {
        if (follower == null || follower.getId() == null) {
            throw new IllegalArgumentException("follower is null or unknown user.");
        }
        if (following == null || following.getId() == null) {
            throw new IllegalArgumentException("following is null or unknown user.");
        }

        this.follower = follower;
        this.following = following;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserFollow other && Objects.equals(this.getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
