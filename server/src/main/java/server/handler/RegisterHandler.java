package server.handler;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import service.UserService;

public class RegisterHandler {
    public void handle(UserService userService, Context context) {

        RegisterRequest request = new Gson().fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);

        context.result(new Gson().toJson(result));
    }
}
