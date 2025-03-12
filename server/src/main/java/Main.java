import chess.*;
import dataaccess.ResponseException;
import server.Server;

public class Main {
    public static void main(String[] args) throws ResponseException {
        Server server = new Server();
//        try {
            int port = server.run(8080);

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

//        } catch (Throwable ex) {
//            System.out.println("server was unable to start");
//        }
    }

}