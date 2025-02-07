package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares;

    public ChessBoard() {
        squares = new ChessPiece[8][8];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     *  find the position of the king
     *  loop through the pieces and find the king
     *  pass in a bool if the king is white or black
     */
    public ChessPosition findKingPos (boolean isWhite) {
        if (isWhite) { // we are looking for the black king
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING &&
                        squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK) {
                        return new ChessPosition(i, j);
                    }
                }
            }
        }
        else { // we are looking for the white king
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING &&
                        squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        return new ChessPosition(i, j);
                    }
                }
            }
        }
    }

    /**
     * getOpponentPieces
     */
    public Collection<ChessPiece> getOpponentPieces(boolean isWhite) {
        Collection<ChessPiece> opponentPieces = new ArrayList<>();
        if (isWhite) { // get all the black pieces
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK) {
                        opponentPieces.add(squares[i][j]);
                    }
                }
            }
        }
        else { // get all the white pieces
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        opponentPieces.add(squares[i][j]);
                    }
                }
            }
        }
        return opponentPieces;
    }

    /**
     * isKingInCheck // this is just going to check if the king is in check, but not anything about
     * the king being able to move to get out of this position
     */
    public boolean isKingInCheck (boolean isWhite) {
        ChessPosition kingPos = findKingPos(isWhite);
        for (ChessPiece piece : getOpponentPieces(isWhite)) {
            Collection<ChessMove> moves = piece.pieceMoves(this, findPos(piece));
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ChessPosition findPos(ChessPiece piece) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j].equals(piece)) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // white rooks
        ChessPiece wr1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition wr1Pos = new ChessPosition(1, 1);
        this.addPiece(wr1Pos, wr1);
        ChessPiece wr2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPosition wr2Pos = new ChessPosition(1, 8);
        this.addPiece(wr2Pos, wr2);

        // white knights
        ChessPiece wn1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition wn1Pos = new ChessPosition(1, 2);
        this.addPiece(wn1Pos, wn1);
        ChessPiece wn2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPosition wn2Pos = new ChessPosition(1, 7);
        this.addPiece(wn2Pos, wn2);

        // white bishops
        ChessPiece wb1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition wb1Pos = new ChessPosition(1, 3);
        this.addPiece(wb1Pos, wb1);
        ChessPiece wb2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPosition wb2Pos = new ChessPosition(1, 6);
        this.addPiece(wb2Pos, wb2);

        // white queen
        ChessPiece wq = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPosition wqPos = new ChessPosition(1, 4);
        this.addPiece(wqPos, wq);

        // white king
        ChessPiece wk = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPosition wkPos = new ChessPosition(1, 5);
        this.addPiece(wkPos, wk);

        // white pawns
        for (int i = 1; i < 9; i++) {
            ChessPiece wp = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition wpPos = new ChessPosition(2, i);
            this.addPiece(wpPos, wp);
        }

        /*-----------------------------------*/

        // black rooks
        ChessPiece br1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition br1Pos = new ChessPosition(8, 1);
        this.addPiece(br1Pos, br1);
        ChessPiece br2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition br2Pos = new ChessPosition(8, 8);
        this.addPiece(br2Pos, br2);

        // black knights
        ChessPiece bn1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition bn1Pos = new ChessPosition(8, 2);
        this.addPiece(bn1Pos, bn1);
        ChessPiece bn2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPosition bn2Pos = new ChessPosition(8, 7);
        this.addPiece(bn2Pos, bn2);

        // black bishops
        ChessPiece bb1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition bb1Pos = new ChessPosition(8, 3);
        this.addPiece(bb1Pos, bb1);
        ChessPiece bb2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPosition bb2Pos = new ChessPosition(8, 6);
        this.addPiece(bb2Pos, bb2);

        // black queen
        ChessPiece bq = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPosition bqPos = new ChessPosition(8, 4);
        this.addPiece(bqPos, bq);

        // black king
        ChessPiece bk = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPosition bkPos = new ChessPosition(8, 5);
        this.addPiece(bkPos, bk);

        // black pawns
        for (int i = 1; i < 9; i++) {
            ChessPiece bp = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition bpPos = new ChessPosition(7, i);
            this.addPiece(bpPos, bp);
        }


    }
}
