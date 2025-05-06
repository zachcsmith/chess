package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashSet;

public interface MovementCalculator {
    static boolean inBounds(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return (row <= 8 && row > 0 && col > 0 && col<=8);
    }

    static HashSet<ChessMove> returnMoves(ChessBoard board, ChessPosition position, HashSet<ChessMove> moves, int[][] changes, boolean infinite) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessPiece currentPiece = board.getPiece(position);
        for (int[] item : changes){
            int newRow = currentRow;
            int newCol = currentCol;
            do {
                newRow = newRow + item[0];
                newCol = newCol + item[1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if(!inBounds(newPosition)){
                    break;
                }
                if(board.getPiece(newPosition) != null){
                    if(board.getPiece(newPosition).getTeamColor() == currentPiece.getTeamColor()){
                        break;
                    }else{
                        moves.add(new ChessMove(position, newPosition, null));
                        break;
                    }
                }else{
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }while (infinite);
        }
        return moves;
    }
}
