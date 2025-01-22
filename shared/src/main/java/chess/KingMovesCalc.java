package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalc extends PieceMovesCalculator {
    public KingMovesCalc() {
    }

    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // row++ for loop 3
        /*
        c--, c, c++
         */
        row++;
        col-=2;
        for (int i = 0; i < 3; i++) {
            col++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check if another piece , enemy or friend
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
            // add to array
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        //row for loop 3
        /*
        c--, c, c++
         */
        col-=2;
        for (int i = 0; i < 3; i++) {
            col++;
            // check if current pos
            if (row == myPosition.getRow() && col == myPosition.getColumn()) {
                continue;
            }
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check if another piece , enemy or friend
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
            // add to array
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }


        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();

        // row-- for loop
        /*
        c--, c, c++
         */
        row--;
        col-=2;
        for (int i = 0; i < 3; i++) {
            col++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            // check if another piece , enemy or friend
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
            // add to array
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        return possibleMoves; //todo: fix this, it's just a placeholder rn
    }
}
