package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalc extends PieceMovesCalculator {
    public RookMovesCalc() {
    }

    private void makingAMove(ChessBoard board, ChessPosition myPosition, String direction,
                             Collection<ChessMove> possibleMoves) {
        //Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        // going up, row++ col
        while (true) {
            if(direction.equals("up")){
                row++;
            }
            else if (direction.equals("down")) {
                row--;
            }
            else if (direction.equals("left")) {
                col--;
            }
            else if (direction.equals("right")) {
                col++;
            }
            if (makeMove(board, myPosition, possibleMoves, row, col, myPiece)) {
                break;
            }
        }
    }

    static boolean makeMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int row, int col, ChessPiece myPiece) {
        if (row <= 0 || row > 8 || col <= 0 || col > 8) {
            return true;
        }
        ChessPosition checkPos = new ChessPosition(row, col);
        if (board.getPiece(checkPos) != null) { //if there is something in the position
            if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                ChessMove move = new ChessMove(myPosition, checkPos);
                possibleMoves.add(move);
                return true;
            } else {
                return true;
            }
        }
        ChessPosition newPosition = new ChessPosition(row, col);
        ChessMove move = new ChessMove(myPosition, newPosition);
        possibleMoves.add(move);
        return false;
    }

    @Override
    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        // going up, row++ col
        makingAMove(board, myPosition, "up", possibleMoves);

        // going down, row-- col
        makingAMove(board, myPosition, "down", possibleMoves);

        // going left, row col--
        makingAMove(board, myPosition, "left", possibleMoves);

        // going right, row col++
        makingAMove(board, myPosition, "right", possibleMoves);

        return possibleMoves;
    }
}
