package sample.shirohoo.realworld.core.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ArticleFacetsTest {
    @Test
    void page_can_not_be_negative() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, -1, 0));
    }

    @Test
    void page_size_can_not_be_negative() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, 0, -1));
    }

    @Test
    void page_size_can_not_be_greater_then_50() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, 0, 51));
    }
}
