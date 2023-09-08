package io.shirohoo.realworld.application.order.service;


import io.shirohoo.realworld.application.order.controller.CreateOrderRequest;
import io.shirohoo.realworld.domain.article.Article;
import io.shirohoo.realworld.domain.order.OrderArticle;
import io.shirohoo.realworld.domain.order.OrderArticleRepository;
import io.shirohoo.realworld.domain.order.OrderRepository;
import io.shirohoo.realworld.domain.order.Orders;
import org.springframework.stereotype.Service;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderArticleRepository orderArticleRepository;

    public OrderService(OrderRepository orderRepository, OrderArticleRepository orderArticleRepository) {
            this.orderRepository = orderRepository;

        this.orderArticleRepository = orderArticleRepository;
    }

    public Orders addArticleToOrder(Article article, Orders order){
        OrderArticle orderArticle = new OrderArticle(order, article);
        orderArticleRepository.save(orderArticle);
        order.addOrderArticle(orderArticle);
        calculatePrice(order);
        return order;


    }


    public void updateOrder(Orders order){
        orderRepository.save(order);
    }

    private void calculatePrice(Orders order) {
        int pricePerArticle = 50;
        int physicalShippingPrice = 100;
        int totalPrice = 0;

        for (OrderArticle orderArticle :
            order.getOrderArticles()) {
            totalPrice += pricePerArticle;
        }
        if(order.getSnailMailAddress() != null){
            totalPrice += physicalShippingPrice;
        }
        order.setPrice(totalPrice);
    }


    public Orders createOrder(CreateOrderRequest createOrderRequest){

        Orders orders = new Orders();

        orders.setUser_id(createOrderRequest.getUser().getId());
        orders.setEmail(createOrderRequest.getEmailAddress());
        orders.setProcessed(false);
        orders.setSnailMailAddress(createOrderRequest.getSnailMailAddress());

        if(orders.getSnailMailAddress() == null) {
            orders.setPrice(500);
        }else {
            orders.setPrice(1000);
        }

        orderRepository.save(orders);


        return orders;

    }

    public OrderRepository orderRepository() {
        return orderRepository;
    }

    public OrderArticleRepository getOrderArticleRepository() {
        return orderArticleRepository;
    }
}
