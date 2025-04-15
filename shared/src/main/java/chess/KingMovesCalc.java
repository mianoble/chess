package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalc extends PieceMovesCalculator {
    public KingMovesCalc() {
    }


//    @Override
//    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> possibleMoves = new ArrayList<>();
//        int row = myPosition.getRow();
//        int col = myPosition.getColumn();
//        ChessPiece myPiece = board.getPiece(myPosition);
//
//        row -= 2;
//        for (int i = 0; i < 3; i++) {
//            row++;
//            col = myPosition.getColumn();
//            col -= 2;
//            for (int j = 0; j < 3; j++) {
//                col++;
//                // check bounds
//                if (row <= 0 || row > 8 || col <= 0 || col > 8) {
//                    break;
//                }
//                // check if filled
//                ChessPosition checkPos = new ChessPosition(row, col);
//                if (board.getPiece(checkPos) != null) {
//                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
//                        ChessMove move = new ChessMove(myPosition, checkPos);
//                        possibleMoves.add(move);
//                        break;
//                    } else if (row == myPosition.getRow() && col == myPosition.getColumn()) {
//                        continue;
//                    } else {
//                        break;
//                    }
//                }
//
//                ChessPosition newPos = new ChessPosition(row, col);
//                ChessMove move = new ChessMove(myPosition, newPos);
//                possibleMoves.add(move);
//            }
//        }
//        return possibleMoves;
//    }


    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                // Skip the current position
                if (dRow == 0 && dCol == 0) continue;

                int newRow = row + dRow;
                int newCol = col + dCol;

                // Check board bounds
                if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) continue;

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece occupyingPiece = board.getPiece(newPos);

                if (occupyingPiece == null || occupyingPiece.getTeamColor() != myPiece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(myPosition, newPos));
                }
            }
        }

        return possibleMoves;
    }
}