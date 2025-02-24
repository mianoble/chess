package chess;

import java.util.ArrayList;
import java.util.Collection;


public class PieceMovesCalculator {
    public PieceMovesCalculator() {

    }

    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING) {
            KingMovesCalc kingMovesCalc = new KingMovesCalc();
            return kingMovesCalc.pieceMovesCalc(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.QUEEN) {
            QueenMovesCalc queenMovesCalc = new QueenMovesCalc();
            return queenMovesCalc.pieceMovesCalc(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP) {
            BishopMovesCalc bishopMovesCalc = new BishopMovesCalc();
            return bishopMovesCalc.pieceMovesCalc(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KNIGHT) {
            KnightMovesCalc knightMovesCalc = new KnightMovesCalc();
            return knightMovesCalc.pieceMovesCalc(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK) {
            RookMovesCalc rookMovesCalc = new RookMovesCalc();
            return rookMovesCalc.pieceMovesCalc(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
            PawnMovesCalc pawnMovesCalc = new PawnMovesCalc();
            return pawnMovesCalc.pieceMovesCalc(board, myPosition);
        }

        return new ArrayList<ChessMove>();

    }
}


