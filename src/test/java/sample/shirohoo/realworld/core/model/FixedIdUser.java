package sample.shirohoo.realworld.core.model;

import java.util.UUID;

final class FixedIdUser extends User {
    @Override
    public UUID getId() {
        return UUID.fromString("cc05ee33-9c6e-41bc-8f77-8d4dbb83f151");
    }
}
