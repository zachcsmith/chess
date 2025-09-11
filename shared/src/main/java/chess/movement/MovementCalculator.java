package chess.movement;

import chess.ChessPosition;

public interface MovementCalculator {
    static boolean inBounds(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row < 1 || row > 8) {
            return false;
        }
        if (col < 1 || col > 8) {
            return false;
        }
        return true;
    }
}
