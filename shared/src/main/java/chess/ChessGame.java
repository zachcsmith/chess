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
    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();
    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
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
        if(board.getPiece(startPosition) == null){
            return null;
        }
        ChessPiece piece = board.getPiece(startPosition);
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        HashSet<ChessMove> moves = new HashSet<>();
        for(ChessMove move: allMoves){
            ChessPosition end = move.getEndPosition();
            ChessPiece capture = board.getPiece(end);
            board.addPiece(startPosition, null);
            board.addPiece(end, piece);
            if(!isInCheck(color)){
                moves.add(move);
            }
            board.addPiece(startPosition, piece);
            board.addPiece(end, capture);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null){throw new InvalidMoveException("no piece at start position");}
        if(piece.getTeamColor() != teamTurn){throw new InvalidMoveException("not this team's turn");}
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if(!moves.contains(move)){throw new InvalidMoveException("move is invalid");}
        ChessPosition end = move.getEndPosition();
        ChessPosition start = move.getStartPosition();
        board.addPiece(start, null);
        if(move.getPromotionPiece() != null){
            ChessPiece.PieceType type = move.getPromotionPiece();
            piece = new ChessPiece(getTeamTurn(), type);
        }
        board.addPiece(end, piece);
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }else{setTeamTurn(TeamColor.WHITE);}


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor oppColor = TeamColor.WHITE;
        ChessPosition kingPos = null;
        if(teamColor == TeamColor.WHITE){
            oppColor = TeamColor.BLACK;
        }
        for (int row = 1; row<=8; row++){
            for(int col = 1; col <= 8; col ++){
                ChessPosition position = new ChessPosition(row, col);
                if(board.getPiece(position) == null){continue;}
                ChessPiece piece = board.getPiece(position);
                if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() != oppColor){
                    kingPos = new ChessPosition(row, col);
                    break;
                }
            }
        }
        for (int row = 1; row<=8; row++){
            for(int col = 1; col <= 8; col ++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if(piece == null || piece.getTeamColor() != oppColor){
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, position);
                for (ChessMove move: moves){
                    if (move.getEndPosition().equals(kingPos)){
                        return true;
                    }
                }

            }
        }return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){return false;}
        for(int row = 1; row <= 8; row ++){
            for (int col = 1; col <= 8; col ++){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece == null || piece.getTeamColor() != teamColor){continue;}
                Collection<ChessMove> moves = validMoves(new ChessPosition(row, col));
                if(!moves.isEmpty()){return false;}
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        if (isInCheck(teamColor)){return false;}
//        for(int row = 1; row <= 8; row ++){
//            for (int col = 1; col <= 8; col ++){
//                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                if(piece == null || piece.getTeamColor() != teamColor){continue;}
//                Collection<ChessMove> moves = validMoves(new ChessPosition(row, col));
//                if(!moves.isEmpty()){return false;}
//            }
//        }
//        return true;
        throw new RuntimeException("Not implemented");
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
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}

