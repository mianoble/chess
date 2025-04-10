package websocket.commands;

public class ConnectCommand extends UserGameCommand{
    public enum Role { PLAYER, SPECTATOR };
    public enum Color { WHITE, BLACK, NONE};

    private final String user;
    private final Role role;
    private final Color color;

    public ConnectCommand(String auth, int gameID, String user, Role r, Color c) {
        super(CommandType.CONNECT, auth, gameID);
        this.user = user;
        this.role = r;
        this.color = c;
    }

    public String getUsername() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public Color getColor() {
        return color;
    }
}
