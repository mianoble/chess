package websocket.commands;

public class ResignCommand extends UserGameCommand {

    public ResignCommand(String auth, int gameID) {
        super(CommandType.RESIGN, auth, gameID);
    }
}
