package sample.shirohoo.realworld.api.response;

public record UsersResponse(UserResponse user) {
  public static UsersResponse from(String token, sample.shirohoo.realworld.core.model.User user) {
    return new UsersResponse(
        new UserResponse(
            user.getEmail(), token, user.getUsername(), user.getBio(), user.getImageUrl()));
  }
}
