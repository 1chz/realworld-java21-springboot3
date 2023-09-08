package io.shirohoo.realworld.domain.order;


import io.shirohoo.realworld.domain.article.Article;
import jakarta.persistence.*;

@Entity
public class OrderArticle {
    @EmbeddedId
    private OrderArticleId id;

    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Orders orders;
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    public Orders getArticleOrder() {
        return orders;
    }

    public void setArticleOrder(Orders orders) {
        this.orders = orders;
    }

    public OrderArticle(Orders orders, Article article) {
        this.id = new OrderArticleId(orders.getId(), article.getId());
        this.orders = orders;
        this.article = article;
    }

    public OrderArticle() {
    }

    public Orders getOrder() {
        return orders;
    }

    public void setOrder(Orders orders) {
        this.orders = orders;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public OrderArticleId getId() {
        return id;
    }

    public void setId(OrderArticleId id) {
        this.id = id;
    }
}
