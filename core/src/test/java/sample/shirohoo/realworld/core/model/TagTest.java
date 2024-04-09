package sample.shirohoo.realworld.core.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TagTest {
    @ParameterizedTest
    @NullAndEmptySource
    void should_throw_exception_if_null_or_empty_tag_names(String tagName) {
        assertThrows(IllegalArgumentException.class, () -> new Tag(tagName));
    }

    @Test
    void equals_is_return_true_if_are_names_same() {
        // given
        Tag tag1 = new TestTag();
        Tag tag2 = new TestTag();

        // when
        boolean isEquals = tag1.equals(tag2);

        // then
        assertTrue(isEquals);
    }

    @Test
    void hashCode_is_return_true_if_are_names_same() {
        // given
        Tag tag1 = new TestTag();
        Tag tag2 = new TestTag();

        // when
        boolean isEquals = tag1.hashCode() == tag2.hashCode();

        // then
        assertTrue(isEquals);
    }
}
