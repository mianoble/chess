package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalc extends PieceMovesCalculator {
    public KingMovesCalc() {
    }

    @Override
    public Collection<ChessMove> pieceMovesCalc(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<ChessMove>(); //todo: fix this, it's just a placeholder rn
    }
}
