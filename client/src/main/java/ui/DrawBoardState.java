package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoardState {
    private final boolean whitePerspective;
    private final ChessBoard board;
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
        } else {
            drawHeader();
            for (int row = 1; row <= 8; row++) {
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
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
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
        out.println();
    }

    private void drawSquare(int row, int col) {
        boolean light;
        light = (row + col) % 2 == 1;
        if (light) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_DARK_GREEN);
        }
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            out.print("   ");
        } else {
            out.print(" " + getPieceKey(piece) + " ");
            out.print(RESET_TEXT_COLOR);
        }
    }

    private String getPieceKey(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            out.print(SET_TEXT_COLOR_RED);
        } else {
            out.print(SET_TEXT_COLOR_BLUE);
        }
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }
}
