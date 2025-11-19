package ui;

import static ui.EscapeSequences.*;

import java.util.*;

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
            try {
                response = eval(line);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public State getState() {
        return state;
    }

    private String register(String[] params) throws ResponseException {
        return "registered?";
    }

    private String help() {
        return "helping";
    }
}
