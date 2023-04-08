package io.github.shirohoo.realworld.domain.user;

import java.io.Serializable;
import java.util.UUID;

class FollowingPK implements Serializable {
    private UUID me;
    private UUID following;
}
