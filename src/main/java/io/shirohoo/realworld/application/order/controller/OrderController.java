package io.shirohoo.realworld.application.order.controller;

import io.shirohoo.realworld.domain.order.OrderRepository;
import io.shirohoo.realworld.domain.order.Orders;
import org.springframework.stereotype.Component;

@Component

public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }
    public void saveOrder(Orders orders) {

        orderRepository.save(orders);

    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
}
