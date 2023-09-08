package io.shirohoo.realworld.application.order;

import io.shirohoo.realworld.IntegrationTest;
import io.shirohoo.realworld.domain.article.Article;
import io.shirohoo.realworld.domain.article.ArticleRepository;
import io.shirohoo.realworld.domain.article.Tag;
import io.shirohoo.realworld.domain.article.TagRepository;
import io.shirohoo.realworld.domain.order.ArticleOrder;
import io.shirohoo.realworld.domain.order.OrderArticle;
import io.shirohoo.realworld.domain.order.OrderArticleRepository;
import io.shirohoo.realworld.domain.order.OrderRepository;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@IntegrationTest
@DisplayName("The order service")
public class ArticleOrderServiceTest {

@Autowired
    ArticleRepository articleRepository;
@Autowired
    UserRepository userRepository;
@Autowired
    OrderRepository orderRepository;
@Autowired
    TagRepository tagRepository;
@Autowired
    OrderArticleRepository orderArticleRepository;

    private Article effectiveJava;
    private Article unEffectiveJava;
    private Article tdd;
    private User james;
    private User simpson;

    @BeforeEach
    void setUp() throws Exception {

        james = User.builder()
            .email("james@example.com")
            .username("james")
            .password("password")
            .build();
        userRepository.save(james);

        simpson = User.builder()
            .email("simpson@example.com")
            .username("simpson")
            .password("password")
            .build();
        userRepository.save(simpson);

        Tag java = new Tag("java");
        Tag testing = new Tag("testing");
        tagRepository.save(java);
        tagRepository.save(testing);

        effectiveJava = Article.builder()
            .description("description")
            .title("Effective Java")
            .author(james)
            .content("content")
            .build();
        java.addTo(effectiveJava);
        articleRepository.save(effectiveJava);

        unEffectiveJava = Article.builder()
            .description("description")
            .title("Uneffective Java is fun")
            .author(simpson)
            .content("article goes here?")
            .build();

        articleRepository.save(unEffectiveJava);

        tdd = Article.builder()
            .description("description")
            .title("TDD- Java is fun")
            .author(simpson)
            .content("article goes here?")
            .build();

        articleRepository.save(tdd);

    }


     @Test
     @DisplayName("Should create an order with multiple articles")
     public void createOrderWithMultipleArticles(){

        ArticleOrder articleOrder = new ArticleOrder();
        articleOrder.setEmail(simpson.getEmail());
        articleOrder.setUser_id(UUID.fromString("bf46c3cb-6215-4748-a09f-136da25bd183"));
        orderRepository.save(articleOrder);

         OrderArticle orderArticle = new OrderArticle(articleOrder, effectiveJava);
         orderArticleRepository.save(orderArticle);
         OrderArticle anotherOrderArticle = new OrderArticle(articleOrder, unEffectiveJava);
         orderArticleRepository.save(anotherOrderArticle);
         OrderArticle thirdOrderArticle = new OrderArticle(articleOrder, tdd);
         orderArticleRepository.save(thirdOrderArticle);

         articleOrder.addOrderArticle(orderArticle);
         articleOrder.addOrderArticle(anotherOrderArticle);
         articleOrder.addOrderArticle(thirdOrderArticle);

         orderRepository.save(articleOrder);

         ArticleOrder fetchedArticleOrder = orderRepository.findById(articleOrder.getId()).get();
         assertTrue(fetchedArticleOrder.getOrderArticles().size() > 2);


     }

    @Test
    @DisplayName("Should create an order with one article")
    public void createOrderWithOneArticle(){

        ArticleOrder articleOrder = new ArticleOrder();
        articleOrder.setEmail("email@email.com");
        articleOrder.setProcessed(false);
        articleOrder.setUser_id(UUID.fromString("bf46c3cb-6215-4748-a09f-136da25bd183"));
        orderRepository.save(articleOrder);

        OrderArticle orderArticle = new OrderArticle(articleOrder, effectiveJava);
        orderArticleRepository.save(orderArticle);


        articleOrder.addOrderArticle(orderArticle);
        orderRepository.save(articleOrder);

        ArticleOrder savedOrder = orderRepository.findById(articleOrder.getId()).get();

        assertEquals(savedOrder, articleOrder);


    }

}
