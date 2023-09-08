package io.shirohoo.realworld.domain.order;


import jakarta.persistence.*;

import javax.lang.model.type.ErrorType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
public class Orders {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID user_id;
    private String email;
    private String snailMailAddress;
    private boolean processed;
    private ErrorType errorType;
    private Integer price;
    @OneToMany(mappedBy = "article" )
    private Set<OrderArticle> orderArticles = new HashSet<>();

    public Orders() {
    }

    public Integer getId() {
        return id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public String getSnailMailAddress() {
        return snailMailAddress;
    }

    public boolean isProcessed() {
        return processed;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSnailMailAddress(String snailMailAddress) {
        this.snailMailAddress = snailMailAddress;
    }

        public void addOrderArticle(OrderArticle orderArticle) {
        this.orderArticles.add(orderArticle);

    }
    public Set<OrderArticle> getOrderArticles() {
        return orderArticles;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }


    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    enum ErrorType{
        UNKNOWN, INVALID_ADDRESS
    }
}

