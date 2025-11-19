package ui;

public class ChessClient {
    private State state = State.LOGGED_OUT;
    private ServerFacade facade;

    public ChessClient(String port) {
        facade = new ServerFacade(port);
    }

    public State getState() {
        return state;
    }
}
