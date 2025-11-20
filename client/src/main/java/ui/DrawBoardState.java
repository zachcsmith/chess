package ui;

import chess.ChessBoard;

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
    }
}
