package ui;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class ChessClient {
    private State state = State.LOGGED_OUT;
    private ServerFacade facade;
    Scanner scanner = new Scanner(System.in);

    public ChessClient(String port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to Chess: type help to begin.");
        var response = "";
        while (!response.equals("quit")) {
            System.out.print("\n" + RESET_TEXT_COLOR + getState() + ">>> " + SET_TEXT_COLOR_GREEN);
            String line = scanner.nextLine();
            System.out.print(line);
        }
    }

    public State getState() {
        return state;
    }
}
