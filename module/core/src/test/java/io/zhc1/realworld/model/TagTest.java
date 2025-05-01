package io.zhc1.realworld.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("Tag - Creation and Equality Validation")
class TagTest {
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Creating tag with null or empty name should throw exception")
    void whenCreateTagWithNullOrEmptyName_thenShouldThrowException(String tagName) {
        assertThrows(IllegalArgumentException.class, () -> new Tag(tagName));
    }

    @Test
    @DisplayName("Tags with same name should be equal")
    void whenComparingTagsWithSameName_thenShouldBeEqual() {
        // given
        Tag tag1 = new TestTag();
        Tag tag2 = new TestTag();

        // when
        boolean isEquals = tag1.equals(tag2);

        // then
        assertTrue(isEquals);
    }

    @Test
    @DisplayName("Tags with same name should have same hash code")
    void whenComparingHashCodesOfTagsWithSameName_thenShouldBeEqual() {
        // given
        Tag tag1 = new TestTag();
        Tag tag2 = new TestTag();

        // when
        boolean isEquals = tag1.hashCode() == tag2.hashCode();

        // then
        assertTrue(isEquals);
    }
}
