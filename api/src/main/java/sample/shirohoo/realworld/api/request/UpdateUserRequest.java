package sample.shirohoo.realworld.api.request;

public record UpdateUserRequest(Params user) {
    public record Params(String email, String username, String password, String bio, String image) {}
}
