package io.github.shirohoo.realworld.application.content;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.shirohoo.realworld.domain.content.Article;
import io.github.shirohoo.realworld.domain.content.ArticleRepository;
import io.github.shirohoo.realworld.domain.content.Tag;
import io.github.shirohoo.realworld.domain.content.TagRepository;
import io.github.shirohoo.realworld.domain.user.User;
import io.github.shirohoo.realworld.domain.user.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("게시글 서비스")
class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() throws Exception {
        User james = new User().email("james@gmail.com").username("james");
        userRepository.save(james);

        User simpson = new User().email("simpson@gmail.com").username("simpson");
        userRepository.save(james);

        Tag java = new Tag().name("java");
        tagRepository.save(java);

        articleRepository.save(
                new Article().title("Effective Java").author(james).addTag(java).addFavorite(simpson));
    }

    @MethodSource
    @ParameterizedTest
    @DisplayName("게시글 서비스는 특정 조건으로 게시글들을 조회하는 기능을 제공한다")
    void getArticles(ArticleFacets facets) throws Exception {
        Page<Article> articles = articleService.getArticles(facets);
        List<Article> content = articles.getContent();
        assertThat(content).hasSize(1).extracting(Article::title).containsExactly("Effective Java");
    }

    static Stream<Arguments> getArticles() {
        return Stream.of(
                Arguments.of(new ArticleFacets("java", null, null, 0, 20)),
                Arguments.of(new ArticleFacets(null, "james", null, 0, 20)),
                Arguments.of(new ArticleFacets(null, null, "simpson", 0, 20)),
                Arguments.of(new ArticleFacets("java", "james", "simpson", 0, 20)));
    }
}
