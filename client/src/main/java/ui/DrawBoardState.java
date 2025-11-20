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

        if (whitePerspective) {
            out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE);
            out.print("   ");
        }
    }

    private void drawRow(int row) {
    }
}
