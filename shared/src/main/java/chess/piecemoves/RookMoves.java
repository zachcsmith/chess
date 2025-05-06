package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMoves implements MovementCalculator{
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> moves = new HashSet<>();
        int[][] changes = new int[][] {{0,-1},{0,1},{-1,0},{1,0}};
        return MovementCalculator.returnMoves(board, position, moves, changes, true);
    }
}
