package model;
import chess.ChessGame;

public record GameDataClient(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game) {
}
