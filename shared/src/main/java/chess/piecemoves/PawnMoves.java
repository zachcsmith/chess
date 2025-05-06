package chess.piecemoves;

import chess.*;

import java.util.HashSet;

public class PawnMoves implements MovementCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessPiece currentPiece = board.getPiece(position);
        HashSet<ChessMove> moves = new HashSet<>();
        //White Pawns
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE && currentRow == 2) {
            int newRow = currentRow + 1;
            ChessPosition newPosition = new ChessPosition(newRow, currentCol);
            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
                newPosition = new ChessPosition(newRow + 1, currentCol);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        } else if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            int newRow = currentRow + 1;
            ChessPosition newPosition = new ChessPosition(newRow, currentCol);
            if (board.getPiece(newPosition) == null) {
                addPawnMoves(moves, position, newPosition);
            }
        }
        //Black
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK && currentRow == 7) {
            int newRow = currentRow - 1;
            ChessPosition newPosition = new ChessPosition(newRow, currentCol);
            if (board.getPiece(newPosition) == null) {
                moves.add(new ChessMove(position, newPosition, null));
                newPosition = new ChessPosition(newRow - 1, currentCol);
                if (board.getPiece(newPosition) == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                }
            }
        } else if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            int newRow = currentRow - 1;
            ChessPosition newPosition = new ChessPosition(newRow, currentCol);
            if (board.getPiece(newPosition) == null) {
                addPawnMoves(moves, position, newPosition);
            }
        }

        //Capturing
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            int[][] changes = {{1,1},{1,-1}};
            capture(board,position,changes,currentRow,currentCol,currentPiece,moves);
        }
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK){
            int[][] changes = {{-1,1},{-1,-1}};
            capture(board, position, changes, currentRow, currentCol, currentPiece, moves);
        }


        return moves;
    }

    private static void capture(ChessBoard board,
                                ChessPosition position,
                                int[][] changes,
                                int currentRow,
                                int currentCol,
                                ChessPiece currentPiece,
                                HashSet<ChessMove> moves) {
        for (int[] item : changes) {
            int newRow = currentRow + item[0];
            int newCol = currentCol + item[1];
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (MovementCalculator.inBounds(newPosition)) {
                //check if piece there
                if(board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != currentPiece.getTeamColor()) {
                    addPawnMoves(moves, position, newPosition);
                }
            }
        }
    }

    public static void addPawnMoves(HashSet<ChessMove> moves, ChessPosition position, ChessPosition newPosition) {
        if (!MovementCalculator.inBounds(newPosition)){return;}
        int newRow = newPosition.getRow();
        if (newRow == 1 || newRow == 8){
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
        }else{
            moves.add(new ChessMove(position, newPosition, null));
        }
    }
}
