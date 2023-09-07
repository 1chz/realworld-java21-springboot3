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
    private ArticleOrder articleOrder;
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    public ArticleOrder getArticleOrder() {
        return articleOrder;
    }

    public void setArticleOrder(ArticleOrder articleOrder) {
        this.articleOrder = articleOrder;
    }

    public OrderArticle(ArticleOrder articleOrder, Article article) {
        this.id = new OrderArticleId(articleOrder.getId(), article.getId());
        this.articleOrder = articleOrder;
        this.article = article;
    }

    public OrderArticle() {
    }

    public ArticleOrder getOrder() {
        return articleOrder;
    }

    public void setOrder(ArticleOrder articleOrder) {
        this.articleOrder = articleOrder;
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
