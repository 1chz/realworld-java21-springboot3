package io.github.shirohoo.realworld.application.content;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("게시글 API")
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private String jamesToken;
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

        LoginUserRequest jamesLoginRequest = new LoginUserRequest("james@gmail.com", "1234");
        jamesToken = "Bearer " + userService.login(jamesLoginRequest).token();

        LoginUserRequest simpsonLoginRequest = new LoginUserRequest("simpson@gmail.com", "1234");
        simpsonToken = "Bearer " + userService.login(simpsonLoginRequest).token();
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
    @DisplayName("게시글 API는 인증된 유저가 팔로우중인 유저의 최근 게시글을 조회할 수 있는 API를 제공한다")
    void getFeedArticles() throws Exception {
        mockMvc.perform(get("/api/articles/feed").header("Authorization", simpsonToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.articles[0].author.username").value("james"))
                .andExpect(jsonPath("$.articles[0].favorited").value(true))
                .andExpect(jsonPath("$.articles[0].favoritesCount").value(1))
                .andExpect(jsonPath("$.articles[0].tagList[0]").value("java"));
    }

    @Test
    @DisplayName("게시글 API는 인증된 유저가 게시글을 작성할 수 있는 API를 제공한다")
    void createArticle() throws Exception {
        // given
        CreateArticleRequest request = new CreateArticleRequest(
                "Test Article", "Test description", "Test body", new String[] {"test", "sample"});

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/articles")
                .header("Authorization", jamesToken)
                .content(objectMapper.writeValueAsString(Map.of("article", request)))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article.slug").value("test-article"))
                .andExpect(jsonPath("$.article.title").value("Test Article"))
                .andExpect(jsonPath("$.article.description").value("Test description"))
                .andExpect(jsonPath("$.article.body").value("Test body"))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("test", "sample")))
                .andExpect(jsonPath("$.article.author.username").value("james"));
    }

    @Test
    @DisplayName("게시글 API는 인증된 유저가 게시글을 수정할 수 있는 API를 제공한다")
    public void updateArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest = new CreateArticleRequest(
                "Test Article", "Test description", "Test body", new String[] {"test", "sample"});

        // - get the slug of the article
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // when
        // - update the article
        UpdateArticleRequest updateRequest =
                new UpdateArticleRequest("Updated Title", "Updated description", "Updated body");
        ResultActions resultActions = mockMvc.perform(put("/api/articles/{slug}", slug)
                .header("Authorization", jamesToken)
                .content(objectMapper.writeValueAsString(Map.of("article", updateRequest)))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("updated-title"))
                .andExpect(jsonPath("$.article.title").value("Updated Title"))
                .andExpect(jsonPath("$.article.description").value("Updated description"))
                .andExpect(jsonPath("$.article.body").value("Updated body"));
    }

    @Test
    @DisplayName("게시글 API는 인증된 유저가 게시글을 삭제할 수 있는 API를 제공한다")
    public void deleteArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest = new CreateArticleRequest(
                "Test Article", "Test description", "Test body", new String[] {"test", "sample"});

        // - get the slug of the article
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // when
        ResultActions resultActions =
                mockMvc.perform(delete("/api/articles/{slug}", slug).header("Authorization", jamesToken));

        // then
        resultActions.andExpect(status().isOk());
    }
}
