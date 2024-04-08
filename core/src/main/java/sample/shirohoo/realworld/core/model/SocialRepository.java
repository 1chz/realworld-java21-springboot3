package sample.shirohoo.realworld.core.model;

import java.util.List;

public interface SocialRepository {
    void save(UserFollow userFollow);

    List<UserFollow> findByFollower(User follower);

    void deleteBy(User follower, User following);

    boolean existsBy(User follower, User following);
}
