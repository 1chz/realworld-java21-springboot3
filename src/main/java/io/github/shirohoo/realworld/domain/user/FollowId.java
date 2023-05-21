package io.github.shirohoo.realworld.domain.user;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowId implements Serializable {
    private UUID fromId;
    private UUID toId;
}
