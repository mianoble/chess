package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalc extends PieceMovesCalculator{
    public PawnMovesCalc() {}

    @Override
    public Collection<ChessMove> pieceMovesCalc (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);

        // white pawns
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (row == 2) { // starting position
                for (int i = 1; i < 3; i++) { // check two spaces in front
                    row ++;
                    // check for obstacles
                    ChessPosition checkPos = new ChessPosition(row, col);
                    if (board.getPiece(checkPos) == null) { //if there is something in the position
                        ChessPosition newPosition = new ChessPosition(row, col);
                        ChessMove move = new ChessMove(myPosition, newPosition);
                        possibleMoves.add(move);
                    }
                    else {
                        break;
                    }
                }
                // check two diagonals
                row = myPosition.getRow();
                row++; col--; // diagonal to the left
                ChessPosition checkPos = new ChessPosition(row, col);
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos);
                            possibleMoves.add(move);
                        }
                    }

                    col += 2; // diagonal to the right
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
            }
            else if (row == 7) { // close to other side of the board
                // check if pos in front is open and add with promotion
                row = myPosition.getRow();
                col = myPosition.getColumn();
                row++;
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
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos2) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                            ChessMove moveR = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.ROOK);
                            possibleMoves.add(moveR);
                            ChessMove moveN = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.KNIGHT);
                            possibleMoves.add(moveN);
                            ChessMove moveB = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.BISHOP);
                            possibleMoves.add(moveB);
                            ChessMove moveQ = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.QUEEN);
                            possibleMoves.add(moveQ);
                        }
                    }
                }

                col += 2;
                ChessPosition checkPos3 = new ChessPosition(row, col);
                // check within board bounds
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
            else { // if its not in the starting position
                // check if pos in front is open and add
                row = myPosition.getRow();
                col = myPosition.getColumn();
                row++;
                ChessPosition checkPos = new ChessPosition(row, col);
                if (board.getPiece(checkPos) == null) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                }

                // check if two diagonals is occupied and add
                col--;
                ChessPosition checkPos2 = new ChessPosition(row, col);
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos2) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos2);
                            possibleMoves.add(move);
                        }
                    }
                }

                col+=2;
                ChessPosition checkPos3 = new ChessPosition(row, col);
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos3) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos3).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos3);
                            possibleMoves.add(move);
                        }
                    }
                }
            }
        }

        // black pawns
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (row == 7) { // starting position
                for (int i = 1; i < 3; i++) { // check two spaces in front
                    row --;
                    // check for obstacles
                    ChessPosition checkPos = new ChessPosition(row, col);
                    if (board.getPiece(checkPos) == null) { //if there is something in the position
                        ChessPosition newPosition = new ChessPosition(row, col);
                        ChessMove move = new ChessMove(myPosition, newPosition);
                        possibleMoves.add(move);
                    }
                    else {
                        break;
                    }

                }
                // check two diagonals
                row = myPosition.getRow();
                row--; col--; // down to the left
                ChessPosition checkPos = new ChessPosition(row, col);
                // check within board bounds
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
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos2) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos2);
                            possibleMoves.add(move);
                        }
                    }
                }
            }
            else if (row == 2){ // close to other side of the board
                // check if pos in front is open and add with promotion
                row = myPosition.getRow();
                col = myPosition.getColumn();
                row--;
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
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos2) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                            ChessMove moveR = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.ROOK);
                            possibleMoves.add(moveR);
                            ChessMove moveN = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.KNIGHT);
                            possibleMoves.add(moveN);
                            ChessMove moveB = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.BISHOP);
                            possibleMoves.add(moveB);
                            ChessMove moveQ = new ChessMove(myPosition, checkPos2, ChessPiece.PieceType.QUEEN);
                            possibleMoves.add(moveQ);
                        }
                    }
                }

                col+=2;
                ChessPosition checkPos3 = new ChessPosition(row, col);
                // check within board bounds
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
            else { // if its not in the starting position
                // check if pos in front is open and add
                row = myPosition.getRow();
                col = myPosition.getColumn();
                row--;
                ChessPosition checkPos = new ChessPosition(row, col);
                if (board.getPiece(checkPos) == null) {
                    ChessMove move = new ChessMove(myPosition, checkPos);
                    possibleMoves.add(move);
                }

                // check if two diagonals is occupied and add
                col--;
                ChessPosition checkPos2 = new ChessPosition(row, col);
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos2) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos2).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos2);
                            possibleMoves.add(move);
                        }
                    }
                }

                col+=2;
                ChessPosition checkPos3 = new ChessPosition(row, col);
                // check within board bounds
                if (row > 0 && row <= 8 && col > 0 && col <= 8) {
                    if (board.getPiece(checkPos3) != null) {
                        if (myPiece.getTeamColor() != board.getPiece(checkPos3).getTeamColor()) {
                            ChessMove move = new ChessMove(myPosition, checkPos3);
                            possibleMoves.add(move);
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }
}
