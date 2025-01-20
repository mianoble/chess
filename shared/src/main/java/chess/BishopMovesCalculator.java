package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalculator extends PieceMovesCalculator {
    public BishopMovesCalculator() {

    }

    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // going left and up (row++, col++)
        while (true) {
            row++; col++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            //todo: check for obstacles
            /*
            if () {
                break;
            }
             */
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going right and up (row++, col--)
        while (true) {
            row++; col--;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            //todo: check for obstacles
            /*
            if () {
                break;
            }
             */
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going left and down (row--, col++)
        while (true) {
            row--; col++;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            //todo: check for obstacles
            /*
            if () {
                break;
            }
             */
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }

        // reset row and col
        row = myPosition.getRow();
        col = myPosition.getColumn();
        // going right and down (row--, col--)
        while (true) {
            row--; col--;
            // check within board bounds
            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            //todo: check for obstacles
            /*
            if () {
                break;
            }
             */
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
        return possibleMoves;
    }
}
