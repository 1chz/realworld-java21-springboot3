package sample.shirohoo.realworld.core.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
  void equals_is_return_true_if_are_ids_same() {
    // given
    Tag tag1 = new FixedIdTag();
    Tag tag2 = new FixedIdTag();

    // when
    boolean isEquals = tag1.equals(tag2);

    // then
    assertTrue(isEquals);
  }

  @Test
  void equals_is_return_true_if_are_names_same() {
    // given
    Tag tag1 = new FixedNameTag();
    Tag tag2 = new FixedNameTag();

    // when
    boolean isEquals = tag1.equals(tag2);

    // then
    assertTrue(isEquals);
  }

  @Test
  void equals_is_return_false_if_are_ids_or_names_difference() {
    // given
    Tag tag1 = new Tag("lorem");
    Tag tag2 = new FixedIdTag();

    // when
    boolean isEquals = tag1.equals(tag2);

    // then
    assertFalse(isEquals);
  }

  @Test
  void hashCode_is_return_true_if_are_names_same() {
    // given
    Tag tag1 = new FixedNameTag();
    Tag tag2 = new FixedNameTag();

    // when
    boolean isEquals = tag1.hashCode() == tag2.hashCode();

    // then
    assertTrue(isEquals);
  }
}
