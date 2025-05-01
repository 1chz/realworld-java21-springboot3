package io.zhc1.realworld.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.zhc1.realworld.model.TestUser;
import io.zhc1.realworld.model.User;
import io.zhc1.realworld.model.UserFollow;
import io.zhc1.realworld.model.UserRelationshipRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Relationship - User Following and Relationship Management Operations")
class UserRelationshipServiceTest {
    @InjectMocks
    UserRelationshipService sut;

    @Mock
    UserRelationshipRepository userRelationshipRepository;

    User follower;
    User following;

    @BeforeEach
    void setUp() {
        follower = new TestUser(UUID.randomUUID(), "follower@example.com", "Follower", "passfollower");
        following = new TestUser(UUID.randomUUID(), "following@example.com", "Following", "passfollowing");
    }

    @Test
    @DisplayName("Check if user is following another user should return true when following exists")
    void whenIsFollowingWithExistingRelationship_thenShouldReturnTrue() {
        when(userRelationshipRepository.existsBy(follower, following)).thenReturn(true);
        assertTrue(sut.isFollowing(follower, following));
    }

    @Test
    @DisplayName("Check if user is following another user should return false when following does not exist")
    void whenIsFollowingWithNonExistingRelationship_thenShouldReturnFalse() {
        when(userRelationshipRepository.existsBy(follower, following)).thenReturn(false);
        assertFalse(sut.isFollowing(follower, following));
    }

    @Test
    @DisplayName("Follow user should save relationship when not already following")
    void whenFollowUserNotAlreadyFollowing_thenShouldSaveRelationship() {
        // given
        when(userRelationshipRepository.existsBy(any(User.class), any(User.class)))
                .thenReturn(false);
        doNothing().when(userRelationshipRepository).save(any(UserFollow.class));

        // when
        sut.follow(follower, following);

        // then
        verify(userRelationshipRepository, times(1)).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("Follow user should not save relationship when already following")
    void whenFollowUserAlreadyFollowing_thenShouldNotSaveRelationship() {
        // given
        when(userRelationshipRepository.existsBy(any(User.class), any(User.class)))
                .thenReturn(true);

        // when
        sut.follow(follower, following);

        // then
        verify(userRelationshipRepository, times(0)).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("Unfollow user should delete relationship when already following")
    void whenUnfollowUserAlreadyFollowing_thenShouldDeleteRelationship() {
        // given
        when(userRelationshipRepository.existsBy(any(User.class), any(User.class)))
                .thenReturn(true);
        doNothing().when(userRelationshipRepository).deleteBy(any(User.class), any(User.class));

        // when
        sut.unfollow(follower, following);

        // then
        verify(userRelationshipRepository, times(1)).deleteBy(any(User.class), any(User.class));
    }

    @Test
    @DisplayName("Unfollow user should not delete relationship when not following")
    void whenUnfollowUserNotFollowing_thenShouldNotDeleteRelationship() {
        // given
        when(userRelationshipRepository.existsBy(any(User.class), any(User.class)))
                .thenReturn(false);

        // when
        sut.unfollow(follower, following);

        // then
        verify(userRelationshipRepository, times(0)).deleteBy(any(User.class), any(User.class));
    }
}
