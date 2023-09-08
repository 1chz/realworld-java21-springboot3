package io.shirohoo.realworld.application.order;

import io.shirohoo.realworld.IntegrationTest;
import io.shirohoo.realworld.application.order.controller.CreateOrderRequest;
import io.shirohoo.realworld.application.order.service.OrderService;
import io.shirohoo.realworld.domain.article.Article;
import io.shirohoo.realworld.domain.article.ArticleRepository;
import io.shirohoo.realworld.domain.article.Tag;
import io.shirohoo.realworld.domain.article.TagRepository;
import io.shirohoo.realworld.domain.order.Orders;
import io.shirohoo.realworld.domain.order.OrderArticle;
import io.shirohoo.realworld.domain.order.OrderArticleRepository;
import io.shirohoo.realworld.domain.order.OrderRepository;
import io.shirohoo.realworld.domain.user.User;
import io.shirohoo.realworld.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;


@IntegrationTest
@DisplayName("The order service")
public class OrdersServiceTest {

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
@Autowired
    OrderService orderService;

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
    @DisplayName("Should transform CreateOrderRequest to Order")
    public void saveCreateOrderRequestAsOrder(){


        CreateOrderRequest createOrderRequest = new CreateOrderRequest(james, "Some address", james.getEmail());
        Orders orders = orderService.createOrder(createOrderRequest);
        System.out.println(orders.getId() + " : " + orders.getEmail());

        assertThat(orders.getId(), greaterThan(0));




    }

     @Test
     @DisplayName("Should create an order with multiple articles")
     public void createOrderWithMultipleArticles(){

        Orders orders = new Orders();
        orders.setEmail(simpson.getEmail());
        orders.setUser_id(UUID.fromString("bf46c3cb-6215-4748-a09f-136da25bd183"));
        orderRepository.save(orders);

         OrderArticle orderArticle = new OrderArticle(orders, effectiveJava);
         orderArticleRepository.save(orderArticle);
         OrderArticle anotherOrderArticle = new OrderArticle(orders, unEffectiveJava);
         orderArticleRepository.save(anotherOrderArticle);
         OrderArticle thirdOrderArticle = new OrderArticle(orders, tdd);
         orderArticleRepository.save(thirdOrderArticle);

         orders.addOrderArticle(orderArticle);
         orders.addOrderArticle(anotherOrderArticle);
         orders.addOrderArticle(thirdOrderArticle);

         orderRepository.save(orders);

         Orders fetchedOrders = orderRepository.findById(orders.getId()).get();
         assertTrue(fetchedOrders.getOrderArticles().size() > 2);


     }

    @Test
    @DisplayName("Should create an order with one article")
    public void createOrderWithOneArticle(){

        Orders orders = new Orders();
        orders.setEmail("email@email.com");
        orders.setProcessed(false);
        orders.setUser_id(UUID.fromString("bf46c3cb-6215-4748-a09f-136da25bd183"));
        orderRepository.save(orders);

        OrderArticle orderArticle = new OrderArticle(orders, effectiveJava);
        orderArticleRepository.save(orderArticle);


        orders.addOrderArticle(orderArticle);
        orderRepository.save(orders);

        Orders savedOrder = orderRepository.findById(orders.getId()).get();

        assertEquals(savedOrder, orders);


    }

}
