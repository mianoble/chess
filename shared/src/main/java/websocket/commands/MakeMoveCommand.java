package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private ChessMove move;

    public MakeMoveCommand(String auth, int gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, auth, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
