package dataaccess.authDAO;

import model.data.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auth;

    public MemoryAuthDAO() {
        this.auth = new HashMap<>();
    }

    @Override
    public void createAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auth.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auth.remove(authToken);
    }
}
