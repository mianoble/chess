package websocket.commands;


public class LeaveCommand extends UserGameCommand {

    String username;

    public LeaveCommand(String auth, int gameID, String user) {
        super(CommandType.LEAVE, auth, gameID);
        this.username = user;
    }

    public String getUsername() {
        return username;
    }
}
