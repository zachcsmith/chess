package chess.movement;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMoves implements MovementCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] changes = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        MovementCalculator.collectMoves(board, position, moves, color, changes, true);
        return moves;
    }
}
