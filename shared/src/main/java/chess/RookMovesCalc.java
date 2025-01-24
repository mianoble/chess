package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalc extends PieceMovesCalculator {
    public RookMovesCalc() {

    }

    @Override
    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // going up, row++ col
        while (true) {
            row++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check for obstacles
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                }
                else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        // going down, row-- col
        while (true) {
            row--;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check for obstacles
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                }
                else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        // going left, row col--
        while (true) {
            col--;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check for obstacles
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                }
                else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        // going right, row col++
        while (true) {
            col++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check for obstacles
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                }
                else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        return possibleMoves;
    }
}
