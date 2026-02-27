package server.handler;

import io.javalin.http.Context;

public interface Handler {
    void handle(Context context);
}
