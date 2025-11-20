package ui;

import chess.ChessGame;
import model.*;
import handlers.*;

import static ui.EscapeSequences.*;

import java.util.*;

public class ChessClient {
    private State state = State.LOGGED_OUT;
    private ServerFacade facade;
    Scanner scanner = new Scanner(System.in);
    HashMap<Integer, GameData> gameMap = new HashMap<>();

    public ChessClient(String port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to Chess: type help to begin.");
        var response = "";
        while (!response.equals("quit")) {
            System.out.print("\n" + RESET_TEXT_COLOR + state + ">>> ");
            String line = scanner.nextLine();
            try {
                response = eval(line);
                System.out.print(SET_TEXT_COLOR_GREEN + response);
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
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public boolean loggedIn() {
        return state.equals(State.LOGGED_IN);
    }

    private String help() {
        if (!loggedIn()) {
            return """
                    - help
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        } else if (loggedIn()) {
            return """
                    - help
                    - logout
                    - create <game name>
                    - list
                    - play <game id>
                    - observe <game id>
                    """;
        } else {
            return "";
        }
    }

    private String register(String[] params) throws ResponseException {
        if (loggedIn()) {
            throw new ResponseException("Logout first before creating a new user.");
        } else {
            if (params.length == 3) {
                UserData req = new UserData(params[0], params[1], params[2]);
                AuthData res = facade.register(req);
                state = State.LOGGED_IN;
                return "You have registered as " + params[0];
            }
            throw new ResponseException("Expected: <username> <password> <email>");
        }
    }

    private String login(String[] params) throws ResponseException {
        if (loggedIn()) {
            throw new ResponseException("You are already logged in.");
        } else {
            if (params.length == 2) {
                LoginRequest req = new LoginRequest(params[0], params[1]);
                LoginResult res = facade.login(req);
                state = State.LOGGED_IN;
                return params[0] + " has logged in.";
            }
            throw new ResponseException("Expected: <username> <password>");
        }
    }

    private String logout() throws ResponseException {
        if (!loggedIn()) {
            throw new ResponseException("You are not logged in.");
        } else {
            facade.logout();
            state = State.LOGGED_OUT;
            return "You have logged out.";
        }
    }

    private String create(String[] params) throws ResponseException {
        if (!loggedIn()) {
            throw new ResponseException("you are not logged in.");
        } else {
            if (params.length == 1) {
                CreateGameRequest req = new CreateGameRequest(params[0]);
                CreateGameResult res = facade.create(req);
                return "The game " + params[0] + " was created.";
            }
            throw new ResponseException("Expected: <game name>");
        }
    }

    private String list() throws ResponseException {
        if (!loggedIn()) {
            throw new ResponseException("you are not logged in.");
        }
        ListGamesResult res = facade.list();
        if (res.games().isEmpty()) {
            return "No games exist.";
        }
        int count = 1;
        String white;
        String black;
        for (GameData game : res.games()) {
            String[] players = getPlayers(game);
            gameMap.put(count, game);
            System.out.println(count + ": " + game.gameName());
            System.out.println("White Player: " + players[0] + ", Black Player: " + players[1]);
            System.out.println();
            count++;
        }
        return "All games have been listed";
    }

    private static String[] getPlayers(GameData game) {
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (game.whiteUsername() == null) {
            white = "Empty";
        }
        if (game.blackUsername() == null) {
            black = "Empty";
        }
        return new String[]{white, black};
    }

}
