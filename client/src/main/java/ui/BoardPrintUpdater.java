package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class BoardPrintUpdater {
    ChessGame game;

    public BoardPrintUpdater(ChessGame game) {
        this.game = game;
    }

    public void boardUpdate(ChessGame updatedGame) {
        this.game = updatedGame;
    }

    public void boardPrint(ChessGame.TeamColor teamColor, ChessPosition highlightPos) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        boolean white = teamColor.equals(ChessGame.TeamColor.WHITE);

        // if there is a desired highlightPos, get all the valid moves from .validMoves
            // store it because we will use it later to highlight the boxes
        Collection<ChessMove> possMoves;
        if (highlightPos != null) {
            possMoves = game.validMoves(highlightPos);
        } else {
            possMoves = null;
        }

        Collection<ChessPosition> highlightSquares = new HashSet<>();
        if (possMoves != null) {
            for (ChessMove m : possMoves) {
                highlightSquares.add(m.getEndPosition());
            }
        }
        out.println();
        // print letter headers above board
        printLetters(teamColor);

        // print perspective based on color (must be white or black)
        for (int i = 1; i < 9; i++) {
            // print row number
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);
            int row = white ? (9 - i) : i;
            out.print(row + " ");

            printRows(row, highlightSquares, highlightPos, teamColor);

            // print row number again
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_GREEN);
            out.println(" " + row);
        }

        // print letter headers under board
        printLetters(teamColor);
        out.println();
    }

    // method for printing letter headers
    private void printLetters(ChessGame.TeamColor teamColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            out.print("   a  b  c  d  e  f  g  h ");
        } else if (teamColor.equals(ChessGame.TeamColor.BLACK)) {
            out.print("   h  g  f  e  d  c  b  a ");
        }
        out.print("\n");
    }

    private void printRows(int row, Collection<ChessPosition> highlightSquares, ChessPosition highlightPos, ChessGame.TeamColor teamColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(SET_TEXT_COLOR_BLACK);

        // print each square with correct color and piece
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            for (int col = 1; col < 9; col++) {
                ChessPosition current = new ChessPosition(row, col);
                // set bg color based on current pos
                out.print(findColor(current, highlightSquares, highlightPos));
                out.print(findPiece(current));
            }
        }
        else {
            for (int col = 8; col > 0; col--) {
                ChessPosition current = new ChessPosition(row, col);
                // set bg color based on current pos
                out.print(findColor(current, highlightSquares, highlightPos));
                out.print(findPiece(current));
            }
        }
    }

    private String findPiece(ChessPosition current) {
        ChessPiece currPiece = game.getBoard().getPiece(current);
        if (currPiece == null) {
            return "   ";
        }
        // check color
        if (currPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            ChessPiece.PieceType type = currPiece.getPieceType();
            if (type.equals(ChessPiece.PieceType.PAWN)) {
                return WHITE_PAWN;
            }
            else if (type.equals(ChessPiece.PieceType.KNIGHT)) {
                return WHITE_KNIGHT;
            }
            else if (type.equals(ChessPiece.PieceType.BISHOP)) {
                return WHITE_BISHOP;
            }
            else if (type.equals(ChessPiece.PieceType.ROOK)) {
                return WHITE_ROOK;
            }
            else if (type.equals(ChessPiece.PieceType.QUEEN)) {
                return WHITE_QUEEN;
            }
            else if (type.equals(ChessPiece.PieceType.KING)) {
                return WHITE_KING;
            }
        }
        else {
            ChessPiece.PieceType type = currPiece.getPieceType();
            if (type.equals(ChessPiece.PieceType.PAWN)) {
                return BLACK_PAWN;
            }
            else if (type.equals(ChessPiece.PieceType.KNIGHT)) {
                return BLACK_KNIGHT;
            }
            else if (type.equals(ChessPiece.PieceType.BISHOP)) {
                return BLACK_BISHOP;
            }
            else if (type.equals(ChessPiece.PieceType.ROOK)) {
                return BLACK_ROOK;
            }
            else if (type.equals(ChessPiece.PieceType.QUEEN)) {
                return BLACK_QUEEN;
            }
            else if (type.equals(ChessPiece.PieceType.KING)) {
                return BLACK_KING;
            }
        }
        return "";
    }

    private String findColor(ChessPosition current, Collection<ChessPosition> highlightSquares, ChessPosition highlightPos) {
        // selected square = BLUE
        // valid pos = YELLOW
        // other squares = TAN and DARK_GREEN
        String color = "";

        if (highlightPos != null && current.equals(highlightPos)) {
            color = SET_BG_COLOR_BLUE;
        }
        else if (highlightSquares.contains(current)) {
            color = SET_BG_COLOR_YELLOW;
        }
        else {
            int row = current.getRow();
            int col = current.getColumn();

            boolean isRowEven = row % 2 == 0;
            boolean isColEven = col % 2 == 0;

            if (isRowEven && isColEven) { // both even -> dark green
                color = SET_BG_COLOR_DARK_GREEN;
            }
            else if (!isRowEven && !isColEven) { // both odd -> dark green
                color = SET_BG_COLOR_DARK_GREEN;
            }
            else { // one is odd and one is even
                color = SET_BG_COLOR_TAN;
            }
        }
        return color;
    }

    public ChessGame getGame() {
        return game;
    }
}
