package model;

public record RegisterReq(
        String username,
        String password,
        String email) {
}
