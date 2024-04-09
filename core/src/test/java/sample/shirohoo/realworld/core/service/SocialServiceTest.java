package sample.shirohoo.realworld.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sample.shirohoo.realworld.core.model.SocialRepository;
import sample.shirohoo.realworld.core.model.User;

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
        follower = new User("follower@example.com", "Follower", "passfollower");
        following = new User("following@example.com", "Following", "passfollowing");
    }

    @Test
    void testIsFollowing() {
        when(socialRepository.existsBy(follower, following)).thenReturn(true);
        assertTrue(sut.isFollowing(follower, following));
    }

    @Test
    void testIsNotFollowing() {
        when(socialRepository.existsBy(follower, following)).thenReturn(false);
        assertFalse(sut.isFollowing(follower, following));
    }
}
