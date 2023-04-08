package io.github.shirohoo.realworld.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@IdClass(FollowingPK.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Following {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "me", referencedColumnName = "id")
    private User me;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following", referencedColumnName = "id")
    private User following;
}
