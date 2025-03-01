package Model;


import chess.ChessGame;

public class GameData {
    private final int gameID;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername, ChessGame game, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = game;
        this.gameName = gameName;
    }




    }
