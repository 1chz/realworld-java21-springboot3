package sample.shirohoo.realworld.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class UserFollowTest {
    @Test
    @SuppressWarnings("DataFlowIssue")
    void if_follower_is_null_an_exception_is_thrown() {
        assertThatThrownBy(() -> new UserFollow(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("follower is null or unknown user.");
    }

    @Test
    void if_follower_id_is_null_an_exception_is_thrown() {
        assertThatThrownBy(() -> new UserFollow(new User(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("follower is null or unknown user.");
    }

    @Test
    void if_following_is_null_an_exception_is_thrown() {
        assertThatThrownBy(() -> new UserFollow(new TestUser(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    void if_following_id_is_null_an_exception_is_thrown() {
        assertThatThrownBy(() -> new UserFollow(new TestUser(), new User()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    void equals_is_return_true_if_are_ids_same() {
        assertThat(new TestUserFollow()).isEqualTo(new TestUserFollow());
    }

    @Test
    void equals_is_return_false_if_are_ids_difference() {
        // given
        UserFollow userFollow = new UserFollow(new TestUser(), new TestUser());
        UserFollow fixedIdUserFollow = new TestUserFollow();

        // when
        boolean isEquals = userFollow.equals(fixedIdUserFollow);

        // then
        assertFalse(isEquals);
    }

    @Test
    void hashCode_is_return_true_if_are_ids_same() {
        assertThat(new TestUserFollow().hashCode()).isEqualTo(new TestUserFollow().hashCode());
    }
}
