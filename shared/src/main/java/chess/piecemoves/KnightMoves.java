package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] changes = new int[][] {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
        return MovementCalculator.returnMoves(board, position, moves, changes, false);
    }
}
