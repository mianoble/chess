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

        row -= 2;
        for (int i = 0; i < 3; i++) {
            row++;
            col = myPosition.getColumn();
            col -= 2;
            for (int j = 0; j < 3; j++) {
                col++;
                // check bounds
                if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                    break;
                }
                // check if filled
                ChessPosition checkPos = new ChessPosition(row, col);
                if (board.getPiece(checkPos) != null) {
                    if (board.getPiece(myPosition).getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                        ChessMove move = new ChessMove(myPosition, checkPos);
                        possibleMoves.add(move);
                        break;
                    } else if (row == myPosition.getRow() && col == myPosition.getColumn()) {
                        continue;
                    } else {
                        break;
                    }
                }

                ChessPosition newPos = new ChessPosition(row, col);
                ChessMove move = new ChessMove(myPosition, newPos);
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }
}