package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface MovementCalculator {
    static boolean inBounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return (row <= 8 && row > 0 && col > 0 && col<=8);
    }

    static HashSet<ChessMove> returnMoves(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int[][] changes, boolean infinite) {

    }
}
