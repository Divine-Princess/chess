package service;

import dataaccess.DataAccessException;
import dataaccess.authdao.AuthDAO;
import model.data.AuthData;
import model.result.ClearResult;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public ClearResult clear() throws DataAccessException {
        authDAO.clear();
        return new ClearResult();
    }
}
