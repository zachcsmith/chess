package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;
import handlers.*;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

import java.util.*;

public class ChessClient implements ServerMessageObserver {
    private State state = State.LOGGED_OUT;
    private ServerFacade facade;
    Scanner scanner = new Scanner(System.in);
    String authToken = null;
    Integer currentGame = null;
    ChessGame.TeamColor playerTeam = null;
    HashMap<Integer, GameData> gameMap = new HashMap<>();
    private final WebSocketFacade webSocketFacade;
    ChessGame myGame = null;

    public ChessClient(String port) {
        facade = new ServerFacade(port);
        webSocketFacade = new WebSocketFacade(port, this);
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
                case "observe" -> observe(params);
                case "join" -> join(params);
                case "redraw" -> redrawBoard();
                case "leave" -> leave();
                case "resign" -> resign();
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
                    - join <game id> <team color>
                    - observe <game id>
                    """;
        } else if (state.equals(State.IN_GAME)) {
            return """
                    - help
                    - redraw
                    - leave
                    - move
                    - resign
                    - highlight
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
                authToken = res.authToken();
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

    private String observe(String[] params) {
        if (!loggedIn()) {
            throw new ResponseException("You are not logged in.");
        } else {
            if (params.length == 1) {
                try {
                    int gameNum = Integer.parseInt(params[0]);
                    GameData game = gameMap.get(gameNum);
                    ChessBoard board = game.game().getBoard();
                    DrawBoardState boardPainter = new DrawBoardState(board, true);
                    boardPainter.drawBoard();
                    webSocketFacade.connect(authToken, game.gameID());
                    currentGame = game.gameID();
                    playerTeam = ChessGame.TeamColor.WHITE;
                    state = State.IN_GAME;
                    return "Now observing " + game.gameName();
                } catch (Exception e) {
                    throw new ResponseException("Not a valid ID");
                }
            }
        }
        throw new ResponseException("Expected: <game ID>");
    }

    private String join(String[] params) {
        ChessGame.TeamColor color;
        if (!loggedIn()) {
            throw new ResponseException("You are not logged in.");
        } else {
            if (params.length == 2) {
                try {
                    int gameNum = Integer.parseInt(params[0]);
                    GameData game = gameMap.get(gameNum);
                    if (Objects.equals(params[1], "white")) {
                        color = ChessGame.TeamColor.WHITE;
                    } else if (Objects.equals(params[1], "black")) {
                        color = ChessGame.TeamColor.BLACK;
                    } else {
                        throw new ResponseException("Color must be white or black.");
                    }
                    facade.join(new JoinGameRequest(color, game.gameID()));
                    webSocketFacade.connect(authToken, game.gameID());
                    ChessBoard board = game.game().getBoard();
                    DrawBoardState boardPainter = new DrawBoardState(board, color == ChessGame.TeamColor.WHITE);
//                    boardPainter.drawBoard();
                    state = State.IN_GAME;
                    playerTeam = color;
                    currentGame = game.gameID();
                    return "Joined " + game.gameName() + " as " + params[1];
                } catch (NumberFormatException e) {
                    throw new ResponseException("Invalid ID");
                } catch (NullPointerException e) {
                    if (gameMap == null) {
                        throw new ResponseException("List games to see available games");
                    } else {
                        throw new ResponseException("Invalid ID");
                    }
                }
            }
        }
        throw new ResponseException("Expected: <game id> <team color>");
    }

    public String redrawBoard() {
        if (state != State.IN_GAME) {
            return "Must be in game";
        }
        ChessBoard board = gameMap.get(currentGame).game().getBoard();

        DrawBoardState boardState = new DrawBoardState(board, playerTeam == ChessGame.TeamColor.WHITE);
        boardState.drawBoard();
        return "Board has been redrawn";
    }

    public String leave() {
        if (state != State.IN_GAME) {
            return "Must be in game";
        }
        webSocketFacade.leave(authToken, currentGame);
        state = State.LOGGED_IN;
        return "Left game";
    }

    public String resign() {
        System.out.println("Are you sure? Y/N");
        String res = scanner.nextLine();
        if (res.equals("Y")) {
            webSocketFacade.resign(authToken, currentGame);
            return "You have resigned, the game is over";
        } else {
            return "Resignation cancelled, the game continues";
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    public void displayNotification(String message) {
        System.out.println(message);
    }

    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void loadGame(ChessGame game) {
        myGame = game;
        redrawBoard();
        System.out.print("\n" + RESET_TEXT_COLOR + state + ">>> ");

    }
}
