package sample.shirohoo.realworld.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.shirohoo.realworld.core.model.SocialRepository;
import sample.shirohoo.realworld.core.model.TestUser;
import sample.shirohoo.realworld.core.model.User;
import sample.shirohoo.realworld.core.model.UserFollow;

@ExtendWith(MockitoExtension.class)
class SocialServiceTest {
    @InjectMocks
    SocialService sut;

    @Mock
    SocialRepository socialRepository;

    User follower;
    User following;

    @BeforeEach
    void setUp() {
        follower = new TestUser(UUID.randomUUID(), "follower@example.com", "Follower", "passfollower");
        following = new TestUser(UUID.randomUUID(), "following@example.com", "Following", "passfollowing");
    }

    @Test
    void testIsFollowing_following() {
        when(socialRepository.existsBy(follower, following)).thenReturn(true);
        assertTrue(sut.isFollowing(follower, following));
    }

    @Test
    void testIsFollowing_not_following() {
        when(socialRepository.existsBy(follower, following)).thenReturn(false);
        assertFalse(sut.isFollowing(follower, following));
    }

    @Test
    void testIsNotFollowing_following() {
        when(socialRepository.existsBy(follower, following)).thenReturn(true);
        assertTrue(sut.isFollowing(follower, following));
    }

    @Test
    void testIsNotFollowing_not_following() {
        when(socialRepository.existsBy(follower, following)).thenReturn(false);
        assertFalse(sut.isFollowing(follower, following));
    }

    @Test
    void testFollow_success() {
        // given
        when(socialRepository.existsBy(any(User.class), any(User.class))).thenReturn(false);
        doNothing().when(socialRepository).save(any(UserFollow.class));

        // when
        sut.follow(follower, following);

        // then
        verify(socialRepository, times(1)).save(any(UserFollow.class));
    }

    @Test
    void testFollow_alreadyFollowing() {
        // given
        when(socialRepository.existsBy(any(User.class), any(User.class))).thenReturn(true);

        // when
        sut.follow(follower, following);

        // then
        verify(socialRepository, times(0)).save(any(UserFollow.class));
    }

    @Test
    void testUnfollow_success() {
        // given
        when(socialRepository.existsBy(any(User.class), any(User.class))).thenReturn(true);
        doNothing().when(socialRepository).deleteBy(any(User.class), any(User.class));

        // when
        sut.unfollow(follower, following);

        // then
        verify(socialRepository, times(1)).deleteBy(any(User.class), any(User.class));
    }

    @Test
    void testUnfollow_notFollowing() {
        // given
        when(socialRepository.existsBy(any(User.class), any(User.class))).thenReturn(false);

        // when
        sut.unfollow(follower, following);

        // then
        verify(socialRepository, times(0)).deleteBy(any(User.class), any(User.class));
    }
}
