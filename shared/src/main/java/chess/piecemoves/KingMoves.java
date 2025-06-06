package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KingMoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] changes = new int[][] {{1,-1},{1,1},{-1,1},{-1,-1},{0,-1},{0,1},{-1,0},{1,0}};
        return MovementCalculator.returnMoves(board, position, moves, changes, false);
    }
}
