package io.zhc1.realworld.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Article Facets - Pagination and Filtering Validation")
class ArticleFacetsTest {
    @Test
    @DisplayName("Creating facets with negative page should throw exception")
    void whenCreateFacetsWithNegativePage_thenShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, -1, 0));
    }

    @Test
    @DisplayName("Creating facets with negative page size should throw exception")
    void whenCreateFacetsWithNegativePageSize_thenShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, 0, -1));
    }

    @Test
    @DisplayName("Creating facets with page size greater than 50 should throw exception")
    void whenCreateFacetsWithPageSizeGreaterThan50_thenShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleFacets(null, null, null, 0, 51));
    }
}
