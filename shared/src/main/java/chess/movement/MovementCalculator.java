package chess.movement;

import chess.*;

import java.util.HashSet;

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

    static void collectMoves(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves,
                             ChessGame.TeamColor baseColor, int[][] changes, boolean infinite) {
        for (int[] change : changes) {
            int newRow = position.getRow();
            int newCol = position.getColumn();
            do {
                newRow = newRow + change[0];
                newCol = newCol + change[1];
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (!inBounds(newPos)) {
                    break;
                }
                ChessPiece maybePiece = board.getPiece(newPos);
                if (maybePiece == null) {
                    moves.add(new ChessMove(position, newPos, null));
                } else if (maybePiece.getTeamColor() == baseColor) {
                    break;
                } else {
                    moves.add(new ChessMove(position, newPos, null));
                    break;
                }
            } while (infinite);
        }
    }
}
