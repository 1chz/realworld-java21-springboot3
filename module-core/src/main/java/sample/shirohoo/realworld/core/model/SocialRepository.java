package sample.shirohoo.realworld.core.model;

import java.util.List;

public interface SocialRepository {
  void save(UserFollow userFollow);

  List<UserFollow> findByFollower(User follower);

  void deleteByFollowerAndFollowing(User follower, User following);

  boolean existsByFollowerAndFollowing(User follower, User following);
}
