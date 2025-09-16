package chess.movement;

import chess.*;

import java.util.HashSet;

public class PawnMoves implements MovementCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> moves = new HashSet<>();
        //white pawns advance
        int row = position.getRow();
        int col = position.getColumn();
        int[][] changes = new int[][]{{1, 1}, {1, -1}};
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            changes = new int[][]{{-1, 1}, {-1, -1}};
        }

        ChessGame.TeamColor teamcolor = board.getPiece(position).getTeamColor();
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                ChessPosition newpos = new ChessPosition(3, col);
                ChessPiece maybepiece = board.getPiece(newpos);
                if (maybepiece == null) {
                    addmove(moves, position, newpos);
                    newpos = new ChessPosition(4, col);
                    if (board.getPiece(newpos) == null) {
                        addmove(moves, position, newpos);
                    }

                }
            } else {
                ChessPosition newpos = new ChessPosition(row + 1, col);
                ChessPiece maybepiece = board.getPiece(newpos);
                if (maybepiece == null) {
                    addmove(moves, position, newpos);
                }
            }

            capturelogic(moves, position, board, changes);
        } else {
            if (row == 7) {
                ChessPosition newpos = new ChessPosition(6, col);
                ChessPiece maybepiece = board.getPiece(newpos);
                if (maybepiece == null) {
                    addmove(moves, position, newpos);
                    newpos = new ChessPosition(5, col);
                    if (board.getPiece(newpos) == null) {
                        addmove(moves, position, newpos);
                    }

                }
            } else {
                ChessPosition newpos = new ChessPosition(row - 1, col);
                ChessPiece maybepiece = board.getPiece(newpos);
                if (maybepiece == null) {
                    addmove(moves, position, newpos);
                }
            }

            capturelogic(moves, position, board, changes);
        }


        return moves;
    }

    public static void addmove(HashSet<ChessMove> moves, ChessPosition pos, ChessPosition newpos) {
        if (newpos.getRow() == 8 || newpos.getRow() == 1) {
            moves.add(new ChessMove(pos, newpos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(pos, newpos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(pos, newpos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(pos, newpos, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(pos, newpos, null));
        }
    }

    public static void capturelogic(HashSet<ChessMove> moves, ChessPosition pos, ChessBoard board, int[][] changes) {
        for (int[] change : changes) {
            ChessPosition newpos = new ChessPosition(pos.getRow() + change[0], pos.getColumn() + change[1]);
            if (MovementCalculator.inBounds(newpos) && board.getPiece(newpos) != null) {
                if (board.getPiece(pos).getTeamColor() != board.getPiece(newpos).getTeamColor()) {
                    addmove(moves, pos, newpos);
                }
            }
        }
    }
}
