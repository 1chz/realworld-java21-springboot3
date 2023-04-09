package io.github.shirohoo.realworld.application.content;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.Tag;
import io.github.shirohoo.realworld.domain.content.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("게시글 API")
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() throws Exception {
        User james = new User().email("james@gmail.com").username("james").password("1234");
        userRepository.save(james);

        User simpson = new User().email("simpson@gmail.com").username("simpson").password("1234");
        userRepository.save(james);

        Tag java = new Tag().name("java");
        tagRepository.save(java);

        Article effectiveJava =
                new Article().title("Effective Java").author(james).addTag(java).addFavorite(simpson);
        articleRepository.save(effectiveJava);
    }

    @Test
    @DisplayName("미인증 유저도 게시글 목록을 조건 검색 할 수 있다")
    void getArticles() throws Exception {
        mockMvc.perform(get("/api/articles")
                        .param("tag", "java")
                        .param("author", "james")
                        .param("favorited", "simpson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.articles[0].author.username").value("james"))
                .andExpect(jsonPath("$.articles[0].favorited").value(false))
                .andExpect(jsonPath("$.articles[0].favoritesCount").value(1))
                .andExpect(jsonPath("$.articles[0].tagList[0]").value("java"));
    }
}
