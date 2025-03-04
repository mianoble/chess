package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalc extends PieceMovesCalculator{
    public PawnMovesCalc() {}
    
    private void makeStartingMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
      
        int increment = 0;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            increment = 1;
        } else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            increment = -1;
        }

        for (int i = 1; i < 3; i++) { // check two spaces in front
            row += increment;
            // check for obstacles
            ChessPosition checkPos = new ChessPosition(row, col);
            if (board.getPiece(checkPos) == null) { //if there is something in the position
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessMove move = new ChessMove(myPosition, newPosition);
                possibleMoves.add(move);
            } else {
                break;
            }
        }
        // check two diagonals
        row = myPosition.getRow();
        row+=increment;
        checkDiagonals(board, myPosition, possibleMoves, row, col, myPiece);
    }

    private void checkDiagonals(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int row, int col, ChessPiece myPiece) {
        col--; // down to the left
        ChessPosition checkPos = new ChessPosition(row, col);
        if (row > 0 && row <= 8 && col > 0 && col <= 8) {
            if (board.getPiece(checkPos) != null) {
                if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                }
            }
        }

        col+=2; // diagonal to the right
        ChessPosition checkPos2 = new ChessPosition(row, col);
        if (row > 0 && row <= 8 && col > 0 && col <= 8) {
            if (board.getPiece(checkPos2) != null) {
                if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, checkPos2);
                    possibleMoves.add(move);
                }
            }
        }
    }

    private void moveAtEndOfBoard(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        int increment = 0;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            increment = 1;
        } else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            increment = -1;
        }
        // check if pos in front is open and add with promotion
        row = myPosition.getRow();
        col = myPosition.getColumn();
        row+=increment;
        ChessPosition checkPos = new ChessPosition(row, col);
        if (board.getPiece(checkPos) == null) {
            ChessMove moveR = new ChessMove(myPosition, checkPos, ChessPiece.PieceType.ROOK);
            possibleMoves.add(moveR);
            ChessMove moveN = new ChessMove(myPosition, checkPos, ChessPiece.PieceType.KNIGHT);
            possibleMoves.add(moveN);
            ChessMove moveB = new ChessMove(myPosition, checkPos, ChessPiece.PieceType.BISHOP);
            possibleMoves.add(moveB);
            ChessMove moveQ = new ChessMove(myPosition, checkPos, ChessPiece.PieceType.QUEEN);
            possibleMoves.add(moveQ);
        }

        // check if two diagonals is occupied and add with promotion
        col--;
        ChessPosition checkPos2 = new ChessPosition(row, col);
        // check within board bounds
        addPromotedPiece(board, myPosition, possibleMoves, row, col, myPiece, checkPos2);
        col += 2;
        ChessPosition checkPos3 = new ChessPosition(row, col);
        // check within board bounds
        addPromotedPiece(board, myPosition, possibleMoves, row, col, myPiece, checkPos3);
    }

    private void addPromotedPiece(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int row, int col, ChessPiece myPiece, ChessPosition checkPos3) {
        if (row > 0 && row <= 8 && col > 0 && col <= 8) {
            if (board.getPiece(checkPos3) != null) {
                if (myPiece.getTeamColor() != board.getPiece(checkPos3).getTeamColor()) {
                    ChessMove moveR = new ChessMove(myPosition, checkPos3, ChessPiece.PieceType.ROOK);
                    possibleMoves.add(moveR);
                    ChessMove moveN = new ChessMove(myPosition, checkPos3, ChessPiece.PieceType.KNIGHT);
                    possibleMoves.add(moveN);
                    ChessMove moveB = new ChessMove(myPosition, checkPos3, ChessPiece.PieceType.BISHOP);
                    possibleMoves.add(moveB);
                    ChessMove moveQ = new ChessMove(myPosition, checkPos3, ChessPiece.PieceType.QUEEN);
                    possibleMoves.add(moveQ);
                }
            }
        }
    }

    public void makingAMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        int increment = 0;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            increment = 1;
        } else if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            increment = -1;
        }
        row += increment;
        ChessPosition checkPos = new ChessPosition(row, col);
        if (board.getPiece(checkPos) == null) {
            ChessMove move = new ChessMove(myPosition, checkPos);
            possibleMoves.add(move);
        }

        checkDiagonals(board, myPosition, possibleMoves, row, col, myPiece);
    }

    @Override
    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        // white pawns
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (row == 2) { // starting position
                makeStartingMove(board, myPosition, possibleMoves);
            } else if (row == 7) { // close to other side of the board
                moveAtEndOfBoard(board, myPosition, possibleMoves);
            } else { // if its not in the starting position
                makingAMove(board, myPosition, possibleMoves);
            }
        }
        // black pawns
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 7) { // starting position
                makeStartingMove(board, myPosition, possibleMoves);
            } else if (row == 2){ // close to other side of the board
                moveAtEndOfBoard(board, myPosition, possibleMoves);
            } else { // if its not in the starting position
                makingAMove(board, myPosition, possibleMoves);
            }
        }
        return possibleMoves;
    }
}
