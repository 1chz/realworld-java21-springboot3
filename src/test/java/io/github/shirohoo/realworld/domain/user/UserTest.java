package io.github.shirohoo.realworld.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저")
class UserTest {
    @Test
    @DisplayName("유저끼리는 팔로우, 언팔로우를 할 수 있다")
    void follows() throws Exception {
        // given
        User james = new User().username("james");
        User simpson = new User().username("simpson");

        // when
        james.follow(simpson);

        // then
        assertTrue(james.isFollowing(simpson));

        // when
        james.unfollow(simpson);

        // then
        assertFalse(james.isFollowing(simpson));
    }
}
