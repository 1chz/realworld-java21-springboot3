package sample.shirohoo.realworld.core.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class UserFollowTest {
    @Test
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
        assertThatThrownBy(() -> new UserFollow(new FixedIdUser(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    void if_following_id_is_null_an_exception_is_thrown() {
        assertThatThrownBy(() -> new UserFollow(new FixedIdUser(), new User()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("following is null or unknown user.");
    }

    @Test
    void equals_is_return_true_if_are_ids_same() {
        assertThat(new FixedIdUserFollow()).isEqualTo(new FixedIdUserFollow());
    }

    @Test
    void equals_is_return_false_if_are_ids_difference() {
        // given
        UserFollow userFollow = new UserFollow(new FixedIdUser(), new FixedIdUser());
        UserFollow fixedIdUserFollow = new FixedIdUserFollow();

        // when
        boolean isEquals = userFollow.equals(fixedIdUserFollow);

        // then
        assertFalse(isEquals);
    }

    @Test
    void hashCode_is_return_true_if_are_ids_same() {
        assertThat(new FixedIdUserFollow().hashCode()).isEqualTo(new FixedIdUserFollow().hashCode());
    }
}
