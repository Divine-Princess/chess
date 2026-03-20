package serverfacade;

import com.google.gson.Gson;
import model.request.*;
import model.result.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public ClearResult clear() {
        var request = buildRequest("DELETE", "/db", null, false);
        var response = sendRequest(request);
        return handleServerResponse(response, ClearResult.class);
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        var request = buildRequest("POST", "/user", registerRequest, false);
        var response = sendRequest(request);
        var formattedResponse = handleServerResponse(response, RegisterResult.class);
        assert formattedResponse != null;
        authToken = formattedResponse.authToken();
        return formattedResponse;
    }

    public LoginResult login(LoginRequest loginRequest) {
        var request = buildRequest("POST", "/session", loginRequest, false);
        var response = sendRequest(request);
        return handleServerResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {
        var request = buildRequest("DELETE", "/session", logoutRequest, true);
        var response = sendRequest(request);
        return handleServerResponse(response, LogoutResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        var request = buildRequest("GET", "/game", listGamesRequest, true);
        var response = sendRequest(request);
        return handleServerResponse(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        var request = buildRequest("POST", "/game", createGameRequest, true);
        var response = sendRequest(request);
        return handleServerResponse(response, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        var request = buildRequest("PUT", "/game", joinGameRequest, true);
        var response = sendRequest(request);
        return handleServerResponse(response, JoinGameResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, Boolean needsHeader) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, requestBody(body));

        if (needsHeader && authToken != null) {
            request.setHeader("Authorization", authToken);
        }

        return request.build();
    }

    private HttpRequest.BodyPublisher requestBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        }
        else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws RuntimeException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to send request");
        }
    }

    private <T> T handleServerResponse(HttpResponse<String> serverResponse, Class<T> responseType) throws RuntimeException {
        int status = serverResponse.statusCode();
        if (!(status / 100 == 2)) {
            if (status / 100 == 5) {
                throw new RuntimeException("Server Error");
            }
            else if (status / 100 == 4) {
                throw new RuntimeException("Client Error");
            }
        }
        if (responseType != null) {
            return new Gson().fromJson(serverResponse.body(), responseType);
        }

        return null;
    }

}
