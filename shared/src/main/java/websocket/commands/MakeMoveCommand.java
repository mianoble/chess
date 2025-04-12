package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private ChessMove move;
    private String username;

    public MakeMoveCommand(String auth, int gameID, ChessMove move, String user) {
        super(CommandType.MAKE_MOVE, auth, gameID);
        this.move = move;
        this.username = user;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getUsername() {
        return username;
    }
}
