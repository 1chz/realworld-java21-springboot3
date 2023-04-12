package io.github.shirohoo.realworld.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The User")
class UserTest {
    @Test
    @DisplayName("Users can follow each other.")
    void follows() throws Exception {
        // given
        User james = new User().username("james");
        User simpson = new User().username("simpson");

        // when
        james.follow(simpson);

        // then
        assertTrue(james.isFollowing(simpson));
    }

    @Test
    @DisplayName("Users can unfollow each other.")
    void unfollow() throws Exception {
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
