package io.github.shirohoo.realworld.application.content;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.application.user.LoginUserRequest;
import io.github.shirohoo.realworld.application.user.SignUpUserRequest;
import io.github.shirohoo.realworld.application.user.UserService;
import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.Tag;
import io.github.shirohoo.realworld.domain.content.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;

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
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private String simpsonToken;

    @BeforeEach
    void setUp() throws Exception {
        SignUpUserRequest jamesSignUpRequest = new SignUpUserRequest("james@gmail.com", "james", "1234");
        User james = userService.signUp(jamesSignUpRequest);

        SignUpUserRequest simpsonSignUpRequest = new SignUpUserRequest("simpson@gmail.com", "simpson", "1234");
        User simpson = userService.signUp(simpsonSignUpRequest);

        simpson.follow(james);

        Tag java = new Tag().name("java");
        tagRepository.save(java);

        Article effectiveJava = new Article()
                .slug("effective-java")
                .title("Effective Java")
                .author(james)
                .addTag(java)
                .addFavorite(simpson);
        articleRepository.save(effectiveJava);

        LoginUserRequest simpsonLoginRequest = new LoginUserRequest("simpson@gmail.com", "1234");
        simpsonToken = userService.login(simpsonLoginRequest).token();
    }

    @Test
    @DisplayName("게시글 API는 슬러그로 단일 게시글을 조회할 수 있는 API를 제공한다")
    void getSingleArticles() throws Exception {
        mockMvc.perform(get("/api/articles/effective-java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.title").value("Effective Java"))
                .andExpect(jsonPath("$.article.author.username").value("james"))
                .andExpect(jsonPath("$.article.favorited").value(false))
                .andExpect(jsonPath("$.article.favoritesCount").value(1))
                .andExpect(jsonPath("$.article.tagList[0]").value("java"));
    }

    @Test
    @DisplayName("게시글 API는 미인증 유저도 최근 게시글 목록을 조건 검색 할 수 있는 API를 제공한다")
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

    @Test
    @DisplayName("게시글 API는 팔로우중인 유저가 작성한 최근 게시글 목록을 조회하는 API를 제공한다")
    void getFeedArticles() throws Exception {
        mockMvc.perform(get("/api/articles/feed").header("Authorization", "Bearer " + simpsonToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.articles[0].author.username").value("james"))
                .andExpect(jsonPath("$.articles[0].favorited").value(true))
                .andExpect(jsonPath("$.articles[0].favoritesCount").value(1))
                .andExpect(jsonPath("$.articles[0].tagList[0]").value("java"));
    }
}
