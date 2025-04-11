package websocket.commands;

public class ConnectCommand extends UserGameCommand{
    public enum Role { PLAYER, SPECTATOR };

    private final String user;
    private final Role role;

    public ConnectCommand(String auth, int gameID, String user, Role r) {
        super(CommandType.CONNECT, auth, gameID);
        this.user = user;
        this.role = r;
    }

    public String getUsername() {
        return user;
    }

    public Role getRole() {
        return role;
    }

}
