package io.github.shirohoo.realworld.domain.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String username;

    private String bio;

    private String image;

    @OneToMany(mappedBy = "me")
    private final Set<Follower> followers = new HashSet<>();

    @OneToMany(mappedBy = "me")
    private final Set<Following> followings = new HashSet<>();

    @Transient
    private String token;

    public static User withEmailUsername(String email, String username) {
        return User.builder().email(email).username(username).build();
    }

    public User bindToken(String token) {
        this.token = token;
        return this;
    }

    public Following follow(User to) {
        Following following = new Following(this, to);
        this.followings.add(following);
        return following;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String encoded) {
        this.password = encoded;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User other) return this.id.equals(other.id);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
