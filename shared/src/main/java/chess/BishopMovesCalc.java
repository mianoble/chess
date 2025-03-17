package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalc extends PieceMovesCalculator {
    public BishopMovesCalc() {}

    private void findMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, String dir) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        while (true) {
            if (dir.equals("left and up")) {
                row++; col++;
            } else if (dir.equals("right and up")) {
                row++; col--;
            } else if (dir.equals("left and down")) {
                row--; col++;
            } else if (dir.equals("right and down")) {
                row--; col--;
            }

            if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                break;
            }
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) != null) { //if there is something in the position
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                    break;
                } else {
                    break;
                }
            }
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessMove move = new ChessMove(myPosition, newPosition);
            possibleMoves.add(move);
        }
    }
    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        // going left and up (row++, col++)
        findMoves(board, myPosition, possibleMoves, "left and up");

        // going right and up (row++, col--)
        findMoves(board, myPosition, possibleMoves, "right and up");

        // going left and down (row--, col++)
        findMoves(board, myPosition, possibleMoves, "left and down");

        // going right and down (row--, col--)
        findMoves(board, myPosition, possibleMoves, "right and down");

        return possibleMoves;
    }
}
