package io.github.shirohoo.realworld.application.article;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.shirohoo.realworld.IntegrationTest;
import io.github.shirohoo.realworld.application.article.controller.CreateArticleRequest;
import io.github.shirohoo.realworld.application.article.controller.CreateCommentRequest;
import io.github.shirohoo.realworld.application.article.controller.UpdateArticleRequest;
import io.github.shirohoo.realworld.application.user.controller.LoginUserRequest;
import io.github.shirohoo.realworld.application.user.controller.SignUpUserRequest;
import io.github.shirohoo.realworld.application.user.service.UserService;
import io.github.shirohoo.realworld.domain.article.Article;
import io.github.shirohoo.realworld.domain.article.ArticleRepository;
import io.github.shirohoo.realworld.domain.article.Tag;
import io.github.shirohoo.realworld.domain.article.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@IntegrationTest
@DisplayName("The Article APIs")
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
        SignUpUserRequest jamesSignUpRequest = new SignUpUserRequest("james@example.com", "james", "1234");
        User james = userService.signUp(jamesSignUpRequest);

        SignUpUserRequest simpsonSignUpRequest = new SignUpUserRequest("simpson@example.com", "simpson", "1234");
        User simpson = userService.signUp(simpsonSignUpRequest);

        simpson.follow(james);

        Tag java = new Tag("java");
        tagRepository.save(java);

        Article effectiveJava = Article.builder()
                .title("Effective Java")
                .author(james)
                .build()
                .addTag(java)
                .favorite(simpson);
        articleRepository.save(effectiveJava);

        LoginUserRequest jamesLoginRequest = new LoginUserRequest("james@example.com", "1234");
        jamesToken = "Token " + userService.login(jamesLoginRequest).token();

        LoginUserRequest simpsonLoginRequest = new LoginUserRequest("simpson@example.com", "1234");
        simpsonToken = "Token " + userService.login(simpsonLoginRequest).token();
    }

    @Test
    @DisplayName("provides an API to retrieve a single article by slug.")
    void getSingleArticles() throws Exception {
        mockMvc.perform(get("/api/articles/{slug}", "effective-java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.title").value("Effective Java"))
                .andExpect(jsonPath("$.article.author.username").value("james"))
                .andExpect(jsonPath("$.article.favorited").value(false))
                .andExpect(jsonPath("$.article.favoritesCount").value(1))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("java")))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows unauthenticated users to view recent articles under specific conditions.")
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
                .andExpect(jsonPath("$.articles[0].tagList[0]").value("java"))
                .andDo(print());
    }

    @Test
    @DisplayName(
            "provides an API that allows authenticated users to retrieve recent articles from users they are following.")
    void getFeedArticles() throws Exception {
        mockMvc.perform(get("/api/articles/feed").header("Authorization", simpsonToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles", hasSize(1)))
                .andExpect(jsonPath("$.articles[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.articles[0].author.username").value("james"))
                .andExpect(jsonPath("$.articles[0].favorited").value(true))
                .andExpect(jsonPath("$.articles[0].favoritesCount").value(1))
                .andExpect(jsonPath("$.articles[0].tagList[0]").value("java"))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to create articles.")
    void createArticle() throws Exception {
        // given
        CreateArticleRequest request =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/articles")
                .header("Authorization", jamesToken)
                .content(objectMapper.writeValueAsString(Map.of("article", request)))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("test-article"))
                .andExpect(jsonPath("$.article.title").value("Test Article"))
                .andExpect(jsonPath("$.article.description").value("Test description"))
                .andExpect(jsonPath("$.article.body").value("Test body"))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("test", "sample")))
                .andExpect(jsonPath("$.article.author.username").value("james"))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to edit articles.")
    public void updateArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

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
                .andExpect(jsonPath("$.article.body").value("Updated body"))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to delete articles.")
    public void deleteArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

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
        resultActions.andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("provides an API that write comments on articles.")
    public void createComment() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // - get the slug of the article by james
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // - create a comment by simpson
        CreateCommentRequest request = new CreateCommentRequest("Test comment");

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/articles/{slug}/comments", slug)
                .header("Authorization", simpsonToken)
                .content(objectMapper.writeValueAsString(Map.of("comment", request)))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment.body").value("Test comment"))
                .andExpect(jsonPath("$.comment.author.username").value("simpson"))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that retrieve all comments of a specific article.")
    public void getComments() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // - get the slug of the article by james
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // - create a comment by simpson
        CreateCommentRequest request = new CreateCommentRequest("Test comment");

        mockMvc.perform(post("/api/articles/{slug}/comments", slug)
                .header("Authorization", simpsonToken)
                .content(objectMapper.writeValueAsString(Map.of("comment", request)))
                .contentType(MediaType.APPLICATION_JSON));

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/articles/{slug}/comments", slug));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].body").value("Test comment"))
                .andExpect(jsonPath("$.comments[0].author.username").value("simpson"))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to delete comments on articles.")
    public void deleteComment() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // - get the slug of the article by james
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // - create a comment by simpson
        CreateCommentRequest request = new CreateCommentRequest("Test comment");

        int commentId = JsonPath.parse(mockMvc.perform(post("/api/articles/{slug}/comments", slug)
                                .header("Authorization", simpsonToken)
                                .content(objectMapper.writeValueAsString(Map.of("comment", request)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.comment.id", Integer.class);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/articles/{slug}/comments/{id}", slug, commentId).header("Authorization", simpsonToken));

        // then
        resultActions.andExpect(status().isOk()).andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to favorite articles.")
    public void favoriteArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // - get the slug of the article by james
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
                mockMvc.perform(post("/api/articles/{slug}/favorite", slug).header("Authorization", simpsonToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("test-article"))
                .andExpect(jsonPath("$.article.title").value("Test Article"))
                .andExpect(jsonPath("$.article.description").value("Test description"))
                .andExpect(jsonPath("$.article.body").value("Test body"))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("test", "sample")))
                .andExpect(jsonPath("$.article.favorited").value(true))
                .andExpect(jsonPath("$.article.favoritesCount").value(1))
                .andExpect(jsonPath("$.article.author.username").value("james"))
                .andExpect(jsonPath("$.article.author.following").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("provides an API that allows authenticated users to unfavorite articles.")
    public void unfavoriteArticle() throws Exception {
        // given
        // - create a test article
        CreateArticleRequest createRequest =
                new CreateArticleRequest("Test Article", "Test description", "Test body", List.of("test", "sample"));

        // - get the slug of the article by james
        String slug = JsonPath.parse(mockMvc.perform(post("/api/articles")
                                .header("Authorization", jamesToken)
                                .content(objectMapper.writeValueAsString(Map.of("article", createRequest)))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .read("$.article.slug");

        // - favorite the article by simpson
        mockMvc.perform(post("/api/articles/{slug}/favorite", slug).header("Authorization", simpsonToken));

        // when
        ResultActions resultActions =
                mockMvc.perform(delete("/api/articles/{slug}/favorite", slug).header("Authorization", simpsonToken));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("test-article"))
                .andExpect(jsonPath("$.article.title").value("Test Article"))
                .andExpect(jsonPath("$.article.description").value("Test description"))
                .andExpect(jsonPath("$.article.body").value("Test body"))
                .andExpect(jsonPath("$.article.tagList", containsInAnyOrder("test", "sample")))
                .andExpect(jsonPath("$.article.favorited").value(false))
                .andExpect(jsonPath("$.article.favoritesCount").value(0))
                .andExpect(jsonPath("$.article.author.username").value("james"))
                .andExpect(jsonPath("$.article.author.following").value(true))
                .andDo(print());
    }
}
