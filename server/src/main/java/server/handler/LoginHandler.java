package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.LoginRequest;
import model.result.LoginResult;
import service.UserService;

public class LoginHandler implements Handler {

    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) {

        LoginRequest request = new Gson().fromJson(context.body(), LoginRequest.class);
        LoginResult result = userService.login(request);

        context.result(new Gson().toJson(result));
    }
}
