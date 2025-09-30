package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turnColor;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //initialization
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> allmoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validmoves = new HashSet<>();
        for (ChessMove move : allmoves) {
            //create fake board scenario
            ChessPiece endpiece = board.getPiece(move.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), piece);
            //check if change causes check
            if (!isInCheck(piece.getTeamColor())) {
                validmoves.add(move);
            }
            //return board to proper state
            board.addPiece(startPosition, piece);
            board.addPiece(move.getEndPosition(), endpiece);
        }
        return validmoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        //confirm validity of move
        if (piece == null) {
            throw new InvalidMoveException("No piece at location");
        }
        if (piece.getTeamColor() != turnColor) {
            throw new InvalidMoveException("not this team's turn");
        }
        //make the move
        var valid = validMoves(start);
        if (valid.contains(move)) {
            ChessPiece.PieceType promo = move.getPromotionPiece();
            if (promo != null) {
                piece = new ChessPiece(piece.getTeamColor(), promo);
            }
            board.addPiece(start, null);
            board.addPiece(end, piece);
            //change team turn after a valid move
            if (getTeamTurn() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException("not a valid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find King
        ChessPosition kingpos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingpos = new ChessPosition(row, col);
                    break;
                }
            }
        }
        //go through the moves of all enemy pieces to see if the king is targeted
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }
                Collection<ChessMove> allmoves = piece.pieceMoves(board, pos);
                for (ChessMove move : allmoves) {
                    if (move.getEndPosition().equals(kingpos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //cant be checkmate without check
        if (!isInCheck(teamColor)) {
            return false;
        } else {
            return anyPossibleMoves(teamColor);
        }
    }

    private boolean anyPossibleMoves(TeamColor teamColor) {
        //goes through each location to grab every piece
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                //makes sure the piece is a friendly piece and exists
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                //can that piece move? if yes we have a possible move so return false
                var moves = validMoves(new ChessPosition(row, col));
                if (!moves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if we are in check that can not be stalemate
        if (isInCheck(teamColor)) {
            return false;
        } else {
            return anyPossibleMoves(teamColor);
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turnColor == chessGame.turnColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnColor, board);
    }
}
