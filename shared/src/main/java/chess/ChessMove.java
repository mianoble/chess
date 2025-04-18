package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    @Override
    public String toString() {
        String startAlpha = "";
        if (startPosition.getColumn() == 1) {
            startAlpha = "a";
        } else if (startPosition.getColumn() == 2) {
            startAlpha = "b";
        } else if (startPosition.getColumn() == 3) {
            startAlpha = "c";
        } else if (startPosition.getColumn() == 4) {
            startAlpha = "d";
        } else if (startPosition.getColumn() == 5) {
            startAlpha = "e";
        } else if (startPosition.getColumn() == 6) {
            startAlpha = "f";
        } else if (startPosition.getColumn() == 7) {
            startAlpha = "g";
        } else if (startPosition.getColumn() == 8) {
            startAlpha = "h";
        }

        String endAlpha = "";
        if (endPosition.getColumn() == 1) {
            endAlpha = "a";
        } else if (endPosition.getColumn() == 2) {
            endAlpha = "b";
        } else if (endPosition.getColumn() == 3) {
            endAlpha = "c";
        }else if (endPosition.getColumn() == 4) {
            endAlpha = "d";
        }else if (endPosition.getColumn() == 5) {
            endAlpha = "e";
        }else if (endPosition.getColumn() == 6) {
            endAlpha = "f";
        }else if (endPosition.getColumn() == 7) {
            endAlpha = "g";
        }else if (endPosition.getColumn() == 8) {
            endAlpha = "h";
        }

        String ans = startAlpha + startPosition.getRow() + " to " +
                endAlpha + endPosition.getRow();
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}
