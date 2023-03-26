package io.github.shirohoo.realworld.infrastructure.user;

import io.github.shirohoo.realworld.domain.user.Follow;
import io.github.shirohoo.realworld.domain.user.FollowId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowJpaRepository extends JpaRepository<Follow, FollowId> {}
