package sample.shirohoo.realworld.core.model;

public final class TestArticle extends Article {
    private final Integer id;

    public TestArticle(Integer id, User author, String title, String description, String content) {
        super(author, title, description, content);
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
