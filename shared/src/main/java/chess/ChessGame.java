package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeam = TeamColor.WHITE;
    private ChessBoard thisBoard;

    public ChessGame() {
        thisBoard = new ChessBoard();
        thisBoard.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> possMoves = new ArrayList<>();
        if (this.thisBoard.getPiece(startPosition) == null) {
            return possMoves;
        }
        else {
            ChessPiece currPiece = this.thisBoard.getPiece(startPosition);
            possMoves = currPiece.pieceMoves(this.thisBoard, startPosition);
            possMoves.removeIf(move -> !safeMove(move, currPiece.getTeamColor()));
            return possMoves;
        }
    }

    /**
     * check if this new move is safe
     * check if this move will put u in check.
     */
    public boolean safeMove (ChessMove move, TeamColor color) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece currPiece = this.thisBoard.getPiece(startPosition);
        ChessPiece endPiece = this.thisBoard.getPiece(endPosition);

        thisBoard.addPiece(startPosition, null);
        thisBoard.addPiece(endPosition, currPiece);

        boolean isSafe = !isInCheck(color);

        thisBoard.addPiece(startPosition, currPiece);
        thisBoard.addPiece(endPosition, endPiece);

        return isSafe;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece currPiece = this.thisBoard.getPiece(startPosition);

        if (currPiece == null) {
            throw new InvalidMoveException("Invalid move");
        }
        else if (currPiece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("Invalid move");
        }
        else {
            Collection<ChessMove> validMovesArray = validMoves(startPosition);
            if (validMovesArray.contains(move)) {
                // execute the move
                // check if there's a promotion
                if (move.getPromotionPiece() == null) {
                    thisBoard.addPiece(startPosition, null);
                    thisBoard.addPiece(endPosition, currPiece);
                }
                else { // there is a promotion
                    // new piece
                    ChessPiece promotionPiece = new ChessPiece(currentTeam, move.getPromotionPiece());
                    thisBoard.addPiece(startPosition, null);
                    thisBoard.addPiece(endPosition, promotionPiece);
                }
            }
            else {
                throw new InvalidMoveException("Invalid move");
            }
        }
        if (this.currentTeam == TeamColor.WHITE) {
            currentTeam = TeamColor.BLACK;
        }
        else {
            currentTeam = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean isWhite = false;
        if (teamColor == TeamColor.WHITE) {
            isWhite = true;
        }
        return thisBoard.isKingInCheck(isWhite);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean check = isInCheck(teamColor);
        if (check) {
            return noValidMoves(teamColor);
        }
        return false;
    }

    /**
     * no valid moves
     * returns true if there are no valid moves
     * go through all the pieces on that team and check if there are any valid moves
     */
    public boolean noValidMoves(TeamColor teamColor) {
        Collection <ChessMove> anyMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition position = new ChessPosition(i + 1, j + 1);
                if (thisBoard.getPiece(position) != null &&
                        this.thisBoard.getPiece(position).getTeamColor() == teamColor) { //on the same team
                    anyMoves.addAll(validMoves(position));
                }
            }
        }
        //anyMoves != null &&
        if (!anyMoves.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return noValidMoves(teamColor);
        }
        else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.thisBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.thisBoard;
    }
}
