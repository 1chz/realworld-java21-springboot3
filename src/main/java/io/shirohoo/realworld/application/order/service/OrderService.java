package io.shirohoo.realworld.application.order.service;


import io.shirohoo.realworld.application.order.controller.CreateOrderRequest;
import io.shirohoo.realworld.domain.order.OrderRepository;
import io.shirohoo.realworld.domain.order.Orders;
import org.springframework.stereotype.Service;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;

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
}
