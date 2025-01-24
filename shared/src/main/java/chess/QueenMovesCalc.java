package chess;

import java.util.Collection;
import java.util.ArrayList;

public class QueenMovesCalc extends PieceMovesCalculator{
    public QueenMovesCalc() {

    }

    @Override
    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {

        // same moves as Rook (left right forward backward)
        RookMovesCalc rookMovesCalc = new RookMovesCalc();
        Collection<ChessMove> rookMoves = new ArrayList<>();
        rookMoves = rookMovesCalc.pieceMovesCalc(board, myPosition);

        // same moves as Bishop (diagonal 4 directions)
        BishopMovesCalc bishopMovesCalc = new BishopMovesCalc();
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        bishopMoves = bishopMovesCalc.pieceMovesCalc(board, myPosition);

        Collection<ChessMove> possibleMoves = new ArrayList<>(rookMoves);
        possibleMoves.addAll(bishopMoves);

        return possibleMoves;
    }
}
