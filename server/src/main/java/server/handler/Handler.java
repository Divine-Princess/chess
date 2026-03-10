package server.handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;

public interface Handler {
    void handle(Context context) throws DataAccessException;
}
