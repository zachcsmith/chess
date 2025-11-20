package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoardState {
    private boolean whitePerspective;
    private ChessBoard board;
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    public DrawBoardState(ChessBoard importedBoard, boolean whitePerspective) {
        this.board = importedBoard;
        this.whitePerspective = whitePerspective;
    }

    public void drawBoard() {
        System.out.print(ERASE_SCREEN);
        System.out.println();
        if (whitePerspective) {
            drawHeader();
            for (int row = 8; row >= 1; row--) {
                drawRow(row);
            }
            drawHeader();
        }

    }

    private void drawHeader() {
        String[] headers;
        if (whitePerspective) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        }
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_LIGHT_GREY);
        out.print("   ");
        for (String letter : headers) {
            out.print(" " + letter + " ");
        }
        out.print("   ");
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
        out.println();
    }

    private void drawRow(int row) {
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
        out.print(" " + row + " ");
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);

        int[] order;
        if (whitePerspective) {
            order = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        } else {
            order = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        }
        for (int col : order) {
            drawSquare(row, col);
        }

        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
        out.print(" " + row + " ");
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void drawSquare(int row, int col) {
        boolean light = row + col % 2 == 1;
        if (light) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            out.print(EMPTY);
        } else {
            out.print(getPieceKey(piece));
        }
    }

    private String getPieceKey(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        }
    }
}
