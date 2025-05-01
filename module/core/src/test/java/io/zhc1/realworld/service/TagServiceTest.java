package io.zhc1.realworld.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.zhc1.realworld.model.Tag;
import io.zhc1.realworld.model.TagRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tag - Tag Management and Retrieval Operations")
class TagServiceTest {
    @InjectMocks
    TagService sut;

    @Mock
    TagRepository tagRepository;

    @Test
    @DisplayName("Get all tags should return all available tags")
    void whenGetAllTags_thenShouldReturnAllAvailableTags() {
        // given
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagRepository.findAll()).thenReturn(tags);

        // when
        List<Tag> returnedTags = sut.getAllTags();

        // then
        assertNotNull(returnedTags);
        assertEquals(2, returnedTags.size());
        assertTrue(returnedTags.contains(tag1) && returnedTags.contains(tag2));
    }
}
