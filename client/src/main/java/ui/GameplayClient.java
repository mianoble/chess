package ui;

import dataaccess.ResponseException;
import facade.ServerFacade;
import static ui.EscapeSequences.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GameplayClient {
    ServerFacade server;
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    public GameplayClient(ServerFacade server) {
        this.server = server;
    }

    public String eval (String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd) {
            case "spectate" -> printBoardBlackView();
            case "join" -> joinGame(tokens[2]);
            case "help" -> help();
            case "exit" -> "exit";
            default -> help();
        };
    }

    public String help() {
        return """
                What do you want to do?
                    Exit the game: type "exit"
                """;
    }

    public String joinGame(String playerColor) {
        playerColor = playerColor.toLowerCase();
        if (playerColor.equals("white")) {
            printBoardWhiteView();
        }
        else if (playerColor.equals("black")) {
            printBoardBlackView();
        }
        return "invalidcolor";
    }

    public String printBoardWhiteView() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        out.println();
        drawWhiteView(out);
        return "whiteboard";
    }

    public String printBoardBlackView() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        out.println();
        drawBlackView(out);
        return "blackboard";
    }

    private static void drawWhiteView(PrintStream out) {
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        out.print(SET_TEXT_COLOR_GREEN);
        out.print("   ");
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(cols[boardCol] + "  ");
        }
        out.println();

        for (int squareRow = BOARD_SIZE_IN_SQUARES; squareRow > 0; --squareRow) {
            out.print(SET_TEXT_COLOR_GREEN);
            out.print(squareRow + " ");
            if (squareRow %2 == 0) {
                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                    if (squareRow == 2) { // print white pawns
                        if (boardCol % 2 == 0) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_PAWN);
                        }
                        else {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_PAWN);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                    else if (squareRow == 8) {
                        if (boardCol == 0) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_ROOK);
                        }
                        else if (boardCol == 1) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KNIGHT);
                        }
                        else if (boardCol == 2) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_BISHOP);
                        }
                        else if (boardCol == 3) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_QUEEN);
                        }
                        else if (boardCol == 4) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KING);
                        }
                        else if (boardCol == 5) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_BISHOP);
                        }
                        else if (boardCol == 6) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KNIGHT);
                        }
                        else if (boardCol == 7) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_ROOK);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                    else {
                        if (boardCol % 2 == 0) {
                            setTan(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        } else {
                            setDarkGreen(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                }
            }
            else {
                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                     if (squareRow == 1) {
                        if (boardCol == 0) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_ROOK);
                        } else if (boardCol == 1) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KNIGHT);
                        } else if (boardCol == 2) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_BISHOP);
                        } else if (boardCol == 3) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_QUEEN);
                        } else if (boardCol == 4) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KING);
                        } else if (boardCol == 5) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_BISHOP);
                        } else if (boardCol == 6) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KNIGHT);
                        } else if (boardCol == 7) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_ROOK);
                        }
                         if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                             setBlack(out);
                         }
                    }
                     else if (squareRow == 7) {
                         if (boardCol % 2 == 0) {
                             setDarkGreen(out);
                             out.print(SET_TEXT_COLOR_BLACK);
                             out.print(BLACK_PAWN);
                         }
                         else {
                             setTan(out);
                             out.print(SET_TEXT_COLOR_BLACK);
                             out.print(BLACK_PAWN);
                         }
                         if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                             setBlack(out);
                         }
                     }
                     else {
                         if (boardCol % 2 == 0) {
                             setDarkGreen(out);
                             out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                         } else {
                             setTan(out);
                             out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                         }
                         if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                             setBlack(out);
                         }
                     }
                }
            }
            out.print(SET_TEXT_COLOR_GREEN);
            out.print(" " + squareRow);

            out.println();
        }

        out.print("   ");
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(cols[boardCol] + "  ");
        }
    }

    private static void drawBlackView(PrintStream out) {
        String[] cols = {"h", "g", "f", "e", "d", "c", "b", "a"};
        out.print(SET_TEXT_COLOR_GREEN);
        out.print("   ");
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(cols[boardCol] + "  ");
        }
        out.println();

        for (int squareRow = 1; squareRow <= BOARD_SIZE_IN_SQUARES; ++squareRow) {
            out.print(SET_TEXT_COLOR_GREEN);
            out.print(squareRow + " ");
            if (squareRow %2 == 0) {
                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                    if (squareRow == 2) { // print white pawns
                        if (boardCol % 2 == 0) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_PAWN);
                        }
                        else {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_PAWN);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                    else if (squareRow == 8) {
                        if (boardCol == 0) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_ROOK);
                        }
                        else if (boardCol == 1) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KNIGHT);
                        }
                        else if (boardCol == 2) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_BISHOP);
                        }
                        else if (boardCol == 3) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_QUEEN);
                        }
                        else if (boardCol == 4) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KING);
                        }
                        else if (boardCol == 5) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_BISHOP);
                        }
                        else if (boardCol == 6) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_KNIGHT);
                        }
                        else if (boardCol == 7) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_ROOK);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                    else {
                        if (boardCol % 2 == 0) {
                            setTan(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        } else {
                            setDarkGreen(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                    }
                    if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                        setBlack(out);
                    }
                }
            }
            else {
                for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                    if (squareRow == 1) {
                        if (boardCol == 0) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_ROOK);
                        } else if (boardCol == 1) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KNIGHT);
                        } else if (boardCol == 2) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_BISHOP);
                        } else if (boardCol == 3) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_QUEEN);
                        } else if (boardCol == 4) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KING);
                        } else if (boardCol == 5) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_BISHOP);
                        } else if (boardCol == 6) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_KNIGHT);
                        } else if (boardCol == 7) {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(WHITE_ROOK);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    } else if (squareRow == 7) {
                        if (boardCol % 2 == 0) {
                            setDarkGreen(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_PAWN);
                        } else {
                            setTan(out);
                            out.print(SET_TEXT_COLOR_BLACK);
                            out.print(BLACK_PAWN);
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    } else {
                        if (boardCol % 2 == 0) {
                            setDarkGreen(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        } else {
                            setTan(out);
                            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                        }
                        if (boardCol == BOARD_SIZE_IN_SQUARES - 1) {
                            setBlack(out);
                        }
                    }
                }
            }
            out.print(SET_TEXT_COLOR_GREEN);
            out.print(" " + squareRow);

            out.println();
        }
    }

//    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);
//
//        out.print(player);
//
//        setBlack(out);
//    }
//
//    private static void setWhite(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private static void setTan(PrintStream out) {
        out.print(SET_BG_COLOR_TAN);
        out.print(SET_TEXT_COLOR_TAN);
    }

}