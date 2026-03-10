package server.handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.request.LogoutRequest;
import model.result.LogoutResult;
import service.UserService;

public class LogoutHandler implements Handler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        LogoutRequest request = new LogoutRequest(authToken);

        LogoutResult result = userService.logout(request);

        context.result(new Gson().toJson(result));
    }
}
