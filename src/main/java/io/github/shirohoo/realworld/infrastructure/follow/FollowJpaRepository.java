package io.github.shirohoo.realworld.infrastructure.follow;

import io.github.shirohoo.realworld.domain.follow.Follow;
import io.github.shirohoo.realworld.domain.follow.FollowId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowJpaRepository extends JpaRepository<Follow, FollowId> {}
