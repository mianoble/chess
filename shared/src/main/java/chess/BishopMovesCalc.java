package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalc extends PieceMovesCalculator {
    public BishopMovesCalc() {}

    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        // going left and up (row++, col++)
        while (true) {
            row++; col++;
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                } else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going right and up (row++, col--)
        while (true) {
            row++; col--;
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                } else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going left and down (row--, col++)
        while (true) {
            row--; col++;
            if (row <= 0 || row > 8 || col <= 0 || col > 8)
                break;
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                } else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going right and down (row--, col--)
        while (true) {
            row--; col--;
            if (row <= 0 || row > 8 || col <= 0 || col > 8)
                break;
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                } else
                    break;
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }
}
