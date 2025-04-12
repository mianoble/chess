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
//                case "resign" -> resign();
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

        public GameData findGameData(int gameID) {
            try {
                ListRes res = server.list();
                for (GameData g : res.games()) {
                    if (g.gameID() == gameID) {
                        return g;
                    }
                }
            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String spectate(int gameID) {
            ChessGame game = findGame(gameID);
            boardPrintUpdater = new BoardPrintUpdater(game);
            boardPrintUpdater.boardPrint(ChessGame.TeamColor.WHITE, null);
            return "";
        }

        public String joinGame(int gameID, String playerColor) {
            ChessGame game = findGame(gameID);
            // boardPrintUpdater = new BoardPrintUpdater(game);

            playerColor = playerColor.toLowerCase();
            if (playerColor.equals("white")) {
                // boardPrintUpdater.boardPrint(ChessGame.TeamColor.WHITE, null);
                return "";
            } else if (playerColor.equals("black")) {
                // boardPrintUpdater.boardPrint(ChessGame.TeamColor.BLACK, null);
                return "";
            }
            return "invalidcolor";
        }

        public String makeMoveClient(String... params) throws Exception {
            ChessGame game = findGame(currGameID);
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
//            server.playerLeave(server.getAuthID(), gameID);
            return "left";
        }
//
//        public String resign(int gameID) {
//
//        }



        // old draw board code. to be deleted..
    //    public String joinGame(String playerColor) {
    //        playerColor = playerColor.toLowerCase();
    //        if (playerColor.equals("white")) {
    //            printBoardWhiteView();
    //            return "";
    //        }
    //        else if (playerColor.equals("black")) {
    //            printBoardBlackView();
    //            return "";
    //        }
    //        return "invalidcolor";
    //    }

//        public String printBoardWhiteView() {
//            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//            out.print(ERASE_SCREEN);
//            out.println();
//            drawWhiteView(out);
//            return "";
//        }
//
//        public void printBoardBlackView() {
//            var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//            out.print(ERASE_SCREEN);
//            out.println();
//            drawBlackView(out);
//        }

//        private static void drawWhiteView(PrintStream out) {
//            setBlack(out);
//            String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print("   ");
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                out.print(cols[boardCol] + "  ");
//            }
//            out.println();
//
//            for (int squareRow = BOARD_SIZE_IN_SQUARES; squareRow > 0; --squareRow) {
//                addPiecesForWhite(out, squareRow);
//            }
//            out.print("   ");
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                out.print(cols[boardCol] + "  ");
//            }
//            resetBG(out);
//        }
//
//        private static void addPiecesForWhite(PrintStream out, int squareRow) {
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print(squareRow + " ");
//            if (squareRow %2 == 0) {
//                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                    addWPawnsAndBPiecesForWhite(out, squareRow, boardCol);
//                }
//            }
//            else {
//                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                    addBPawnsAndWPiecesForWhite(out, squareRow, boardCol);
//                }
//            }
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print(" " + squareRow);
//            out.println();
//        }
//
//        private static void drawBlackView(PrintStream out) {
//            setBlack(out);
//            String[] cols = {"h", "g", "f", "e", "d", "c", "b", "a"};
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print("   ");
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                out.print(cols[boardCol] + "  ");
//            }
//            out.println();
//
//            for (int squareRow = 1; squareRow <= BOARD_SIZE_IN_SQUARES; ++squareRow) {
//                addPiecesForBlack(out, squareRow);
//            }
//            out.print("   ");
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                out.print(cols[boardCol] + "  ");
//            }
//        }
//
//        private static void addPiecesForBlack(PrintStream out, int squareRow) {
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print(squareRow + " ");
//            if (squareRow %2 == 0) {
//                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                    addWPawnsAndBPiecesForBlack(out, squareRow, boardCol);
//                }
//            }
//            else {
//                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                    addBPawnsAndWPiecesForBlack(out, squareRow, boardCol);
//                }
//            }
//            out.print(SET_TEXT_COLOR_GREEN);
//            out.print(" " + squareRow);
//            out.println();
//        }
//
//        private static void addWPawnsAndBPiecesForWhite(PrintStream out, int squareRow, int boardCol) {
//            if (squareRow == 2) { // print white pawns
//                blackPawns(out, boardCol, WHITE_PAWN);
//            }
//            else if (squareRow == 8) {
//                if (boardCol == 0) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_ROOK);
//                }
//                else if (boardCol == 1) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KNIGHT);
//                }
//                else if (boardCol == 2) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_BISHOP);
//                }
//                else if (boardCol == 3) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_QUEEN);
//                }
//                else if (boardCol == 4) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KING);
//                }
//                else if (boardCol == 5) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_BISHOP);
//                }
//                else if (boardCol == 6) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KNIGHT);
//                }
//                else if (boardCol == 7) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_ROOK);
//                }
//                if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                    setBlack(out);
//                }
//            }
//            else {
//                emptySquares(out, boardCol);
//            }
//        }
//
//        private static void emptySquares(PrintStream out, int boardCol) {
//            if (boardCol % 2 == 0) {
//                setTan(out);
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//            } else {
//                setDarkGreen(out);
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//            }
//            if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                setBlack(out);
//            }
//        }
//
//        private static void addWPawnsAndBPiecesForBlack(PrintStream out, int squareRow, int boardCol) {
//            if (squareRow == 2) { // print white pawns
//                addEmptySquaresForWhite(out, boardCol, WHITE_PAWN);
//            }
//            else if (squareRow == 8) {
//                if (boardCol == 0) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_ROOK);
//                }
//                else if (boardCol == 1) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KNIGHT);
//                }
//                else if (boardCol == 2) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_BISHOP);
//                }
//                else if (boardCol == 3) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KING);
//                }
//                else if (boardCol == 4) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_QUEEN);
//                }
//                else if (boardCol == 5) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_BISHOP);
//                }
//                else if (boardCol == 6) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_KNIGHT);
//                }
//                else if (boardCol == 7) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(BLACK_ROOK);
//                }
//                if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                    setBlack(out);
//                }
//            }
//            else {
//                addEmptySquaresForBlack(out, boardCol);
//            }
//        }
//
//        private static void addEmptySquaresForBlack(PrintStream out, int boardCol) {
//            if (boardCol % 2 == 0) {
//                setDarkGreen(out);
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//            } else {
//                setTan(out);
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//            }
//            if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                setBlack(out);
//            }
//        }
//
//        private static void addBPawnsAndWPiecesForWhite(PrintStream out, int squareRow, int boardCol) {
//            if (squareRow == 1) {
//                if (boardCol == 0) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_ROOK);
//                } else if (boardCol == 1) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KNIGHT);
//                } else if (boardCol == 2) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_BISHOP);
//                } else if (boardCol == 3) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_QUEEN);
//                } else if (boardCol == 4) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KING);
//                } else if (boardCol == 5) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_BISHOP);
//                } else if (boardCol == 6) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KNIGHT);
//                } else if (boardCol == 7) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_ROOK);
//                }
//                if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                    setBlack(out);
//                }
//            }
//            else if (squareRow == 7) {
//                addEmptySquaresForWhite(out, boardCol, BLACK_PAWN);
//            }
//            else {
//                addEmptySquaresForBlack(out, boardCol);
//            }
//        }
//
//        private static void addEmptySquaresForWhite(PrintStream out, int boardCol, String blackPawn) {
//            if (boardCol % 2 == 0) {
//                setDarkGreen(out);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(blackPawn);
//            }
//            else {
//                setTan(out);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(blackPawn);
//            }
//            if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                setBlack(out);
//            }
//        }
//
//        private static void addBPawnsAndWPiecesForBlack(PrintStream out, int squareRow, int boardCol) {
//            if (squareRow == 1) {
//                if (boardCol == 0) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_ROOK);
//                } else if (boardCol == 1) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KNIGHT);
//                } else if (boardCol == 2) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_BISHOP);
//                } else if (boardCol == 3) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KING);
//                } else if (boardCol == 4) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_QUEEN);
//                } else if (boardCol == 5) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_BISHOP);
//                } else if (boardCol == 6) {
//                    setTan(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_KNIGHT);
//                } else if (boardCol == 7) {
//                    setDarkGreen(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
//                    out.print(WHITE_ROOK);
//                }
//                if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                    setBlack(out);
//                }
//            }
//            else if (squareRow == 7) {
//                blackPawns(out, boardCol, BLACK_PAWN);
//            }
//            else {
//                emptySquares(out, boardCol);
//            }
//        }
//
//        private static void blackPawns(PrintStream out, int boardCol, String blackPawn) {
//            if (boardCol % 2 == 0) {
//                setTan(out);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(blackPawn);
//            }
//            else {
//                setDarkGreen(out);
//                out.print(SET_TEXT_COLOR_BLACK);
//                out.print(blackPawn);
//            }
//            if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
//                setBlack(out);
//            }
//        }
//
//        private static void setBlack(PrintStream out) {
//            out.print(SET_BG_COLOR_BLACK);
//            out.print(SET_TEXT_COLOR_BLACK);
//        }
//
//        private static void resetBG(PrintStream out) {
//            out.print(RESET_BG_COLOR);
//            out.print(RESET_BG_COLOR);
//        }
//
//        private static void setDarkGreen(PrintStream out) {
//            out.print(SET_BG_COLOR_DARK_GREEN);
//            out.print(SET_TEXT_COLOR_DARK_GREEN);
//        }
//
//        private static void setTan(PrintStream out) {
//            out.print(SET_BG_COLOR_TAN);
//            out.print(SET_TEXT_COLOR_TAN);
//        }

    }