package sample.shirohoo.realworld.core.model;

import java.util.UUID;

public final class TestUser extends User {
    private final UUID id;

    public TestUser() {
        this.id = UUID.randomUUID();
    }

    public TestUser(UUID id) {
        this.id = id;
    }

    public TestUser(UUID id, String email, String username, String password) {
        super(email, username, password);
        this.id = id;
    }

    @Override
    public UUID getId() {
        return this.id;
    }
}
