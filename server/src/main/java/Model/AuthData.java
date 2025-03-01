package Model;

public class AuthData {
    private final int authID;
    private final String username;

    public AuthData(int authID, String username) {
        this.authID = authID;
        this.username = username;
    }

    private int getAuthID() {
        return authID;
    }

    private String getUsername() {
        return username;
    }
}
