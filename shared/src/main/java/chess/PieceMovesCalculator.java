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
            return new ArrayList<ChessMove>();
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
            return new ArrayList<ChessMove>();
        }

        return new ArrayList<ChessMove>();

    }
}


