package ui;

import java.net.http.HttpClient;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }


}
