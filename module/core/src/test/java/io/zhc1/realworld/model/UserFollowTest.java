package io.zhc1.realworld.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Follow - Relationship Validation and Equality Testing")
class UserFollowTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    @DisplayName("Creating user follow with null follower should throw exception")
    void whenCreateUserFollowWithNullFollower_thenShouldThrowException() {
        assertThatThrownBy(() -> new UserFollow(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("follower is null or unknown user.");
    }

    @Test
    @DisplayName("Creating user follow with follower having null ID should throw exception")
    void whenCreateUserFollowWithFollowerHavingNullId_thenShouldThrowException() {
        assertThatThrownBy(() -> new UserFollow(new User(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("follower is null or unknown user.");
    }

    @Test
    @DisplayName("Creating user follow with null following should throw exception")
    void whenCreateUserFollowWithNullFollowing_thenShouldThrowException() {
        assertThatThrownBy(() -> new UserFollow(new TestUser(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    @DisplayName("Creating user follow with following having null ID should throw exception")
    void whenCreateUserFollowWithFollowingHavingNullId_thenShouldThrowException() {
        assertThatThrownBy(() -> new UserFollow(new TestUser(), new User()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    @DisplayName("User follows with same IDs should be equal")
    void whenComparingUserFollowsWithSameIds_thenShouldBeEqual() {
        assertThat(new TestUserFollow()).isEqualTo(new TestUserFollow());
    }

    @Test
    @DisplayName("User follows with different IDs should not be equal")
    void whenComparingUserFollowsWithDifferentIds_thenShouldNotBeEqual() {
        // given
        UserFollow userFollow = new UserFollow(new TestUser(), new TestUser());
        UserFollow fixedIdUserFollow = new TestUserFollow();

        // when
        boolean isEquals = userFollow.equals(fixedIdUserFollow);

        // then
        assertFalse(isEquals);
    }

    @Test
    @DisplayName("User follows with same IDs should have same hash code")
    void whenComparingHashCodesOfUserFollowsWithSameIds_thenShouldBeEqual() {
        assertThat(new TestUserFollow().hashCode()).isEqualTo(new TestUserFollow().hashCode());
    }
}
