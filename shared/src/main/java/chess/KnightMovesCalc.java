package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalc extends PieceMovesCalculator{
    public KnightMovesCalc() {

    }

    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        ChessPiece myPiece = board.getPiece(myPosition);


        int[][] moves = {
                {2, -1},
                {2, 1},
                {1, -2},
                {1, 2},
                {-1, -2},
                {-1, 2},
                {-2, -1},
                {-2, 1}
        };

        for (int[] move : moves) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row = row + move[0];
            col = col + move[1];

            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                continue;
            }

            // check if there's another piece , enemy or friend
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove addMove = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(addMove);
                    continue;
                }
            }
            else {// if there is nothing in the position
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessMove addMove = new ChessMove(myPosition, newPosition);
                    possibleMoves.add(addMove);
                    continue;
            }
        }
            //add to list

            //reset row,col


        return possibleMoves;
    }
}
