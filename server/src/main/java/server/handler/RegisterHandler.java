package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import service.UserService;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void handle(Context context) {

        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);

        context.result(new Gson().toJson(result));
    }
}
