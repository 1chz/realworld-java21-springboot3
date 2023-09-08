package io.shirohoo.realworld.application.order.controller;

import io.shirohoo.realworld.domain.user.User;

public class CreateOrderRequest {


    private final User user;
    private final String snailMailAddress;
    private final String emailAddress;

    public CreateOrderRequest(User user, String snailMailAddress , String emailAddress) {


        this.user = user;
        this.snailMailAddress = snailMailAddress;
        this.emailAddress = emailAddress;
    }
    public CreateOrderRequest(User user, String emailAddress) {
        this(user, null, emailAddress);
    }

    public User getUser() {
        return user;
    }

    public String getSnailMailAddress() {
        return snailMailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
