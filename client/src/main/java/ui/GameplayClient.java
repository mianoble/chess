    package ui;

    import chess.*;
    import client.NotificationHandler;
    import client.ServerFacade;
    import client.WebsocketCommunicator;
    import model.GameData;
    import model.ListRes;
    import model.ResponseException;
    import websocket.commands.ConnectCommand;

    import static ui.EscapeSequences.*;

    import java.io.PrintStream;
    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Collection;

    public class GameplayClient {
        private final ServerFacade server;
        private BoardPrintUpdater boardPrintUpdater;
        private final NotificationHandler notificationHandler;
        private WebsocketCommunicator ws;
        private String serverUrl;

        private ChessGame.TeamColor currColor;
        private String currUser;
        private int currGameID;

        // Board dimensions.
        private static final int BOARD_SIZE_IN_SQUARES = 8;
        private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
        private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

        // Padded characters.
        private static final String EMPTY = "   ";

        public GameplayClient(ServerFacade server, NotificationHandler nh) {
            this.server = server;
            serverUrl = server.getServerURL();
            ChessGame tempGame = new ChessGame();
            boardPrintUpdater = new BoardPrintUpdater(tempGame);
            this.notificationHandler = nh;
        }

        public BoardPrintUpdater getBoardPrintUpdater() {
            return boardPrintUpdater;
        }


        public String eval (String input, int gameID, ChessGame.TeamColor color, String user) throws Exception {
            currColor = color;
            currGameID = gameID;
            currUser = user;

            initWebSocket();

            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "spectate" -> spectate(gameID);
                case "join" -> joinGame(gameID, tokens[2]);
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave(gameID);
                case "move" -> makeMoveClient(params);
                case "resign" -> resign(gameID);
    //            case "highlight" -> highlight(tokens[1]);
                case "exit" -> "exit";
                default -> help();
            };
        }

        public void initWebSocket() throws Exception {
            if (ws == null) {
                ws = new WebsocketCommunicator(serverUrl, notificationHandler);
                ws.setTeamColor(currColor);
                ws.setGameplayClient(this);
            }
        }

        public void connectToGame(int gameID, ChessGame.TeamColor color, String username) throws Exception {
            this.currGameID = gameID;
            this.currColor = color;
            this.currUser = username;

            initWebSocket(); // ✅ This sets ws and links it to gameplayClient
            ws.userJoinedAGame(server.getAuthID(), username, gameID, ConnectCommand.Role.PLAYER);
        }

        public String help() {
            return """
                    What do you want to do?
                        Redraw the chess board: type "redraw"
                        Leave the game: type "leave"
                        Make a move: type "move" <ChessMove>
                        Forfeit the game: type "resign"
                        Highlight legal moves: type "highlight" <ChessPiece>
                        Print a list of possible actions: type "help"
                    """;
        }

        public ChessGame findGame(int gameID) {
            try {
                ListRes res = server.list();
                GameData thisGame = null;
                for (GameData g : res.games()) {
                    if (g.gameID() == gameID) {
                        thisGame = g;
                        break;
                    }
                }
                if (thisGame != null) {
                    return thisGame.game();
                } else {
                    return null;
                }
            } catch (ResponseException e) {
                return null;
            }
        }

        public String spectate(int gameID) {
            ChessGame game = findGame(gameID);
            boardPrintUpdater = new BoardPrintUpdater(game);
            boardPrintUpdater.boardPrint(ChessGame.TeamColor.WHITE, null);
            return "";
        }

        public String joinGame(int gameID, String playerColor) {
            ChessGame game = findGame(gameID);
            playerColor = playerColor.toLowerCase();
            if (playerColor.equals("white")) {
                return "";
            } else if (playerColor.equals("black")) {
                return "";
            }
            return "invalidcolor";
        }

        public String makeMoveClient(String... params) throws Exception {
            ChessGame game = findGame(currGameID);
//            ChessGame game = boardPrintUpdater.getGame();
            if (game.isGameOver()){
                return "The game is over. No more moves can be made.";
            }
            if (params.length < 2) {
                return "invalid move request";
            }
            String start = params[0];
            String end = params[1];
            ChessPosition startPos = parseAlgebraic(start);
            ChessPosition endPos = parseAlgebraic(end);

            ChessPiece.PieceType promotion = null;
            if (params.length == 3) {
                promotion = returnType(params[3]);
            }
            // create the move to be used in calculating
            ChessMove move = new ChessMove(startPos, endPos, promotion);

            ws.playerMadeMove(server.getAuthID(), currGameID, move, currUser);
            // how to make real move and print board?
            return "move sent";
        }

        private ChessPosition parseAlgebraic(String algebraic) {
            if (algebraic == null || algebraic.length() != 2) {
                throw new IllegalArgumentException("Invalid move notation: " + algebraic);
            }

            char file = algebraic.charAt(0); // a-h
            char rank = algebraic.charAt(1); // 1-8

            int col = file - 'a' + 1;
            int row = rank - '1' + 1;

            if (col < 1 || col > 8 || row < 1 || row > 8) {
                throw new IllegalArgumentException("Invalid chess position: " + algebraic);
            }

            return new ChessPosition(row, col);
        }

        public ChessPiece.PieceType returnType(String piece) {
            return switch(piece.toUpperCase()) {
                case "PAWN" -> ChessPiece.PieceType.PAWN;
                case "ROOK" -> ChessPiece.PieceType.ROOK;
                case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
                case "BISHOP" -> ChessPiece.PieceType.BISHOP;
                case "KING" -> ChessPiece.PieceType.KING;
                case "QUEEN" -> ChessPiece.PieceType.QUEEN;
                default ->  null;
            };
        }

        public String redraw() {
            boardPrintUpdater.boardPrint(currColor, null);
            return "redrawn";
        }

        public String leave(int gameID) throws Exception {
            ListRes res = server.list();
            GameData game = null;
            for (GameData g : res.games()) {
                if (g.gameID() == gameID) {
                    game = g;
                    break;
                }
            }

            if (game == null) {
                return "game " + gameID + " not found";
            }

            ws.userLeftAGame(server.getAuthID(), gameID, currUser);
            return "left";
        }

        public String resign(int gameID) {
            try {
                ws.sendResign(server.getAuthID(), gameID);
                return "resigned";
            } catch (Exception e) {
                return "failed to resign: " + e.getMessage();
            }
        }


    }