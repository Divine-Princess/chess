package service;

import dataaccess.authdao.AuthDAO;
import model.result.ClearResult;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public ClearResult clear() {
        authDAO.clear();
        return new ClearResult();
    }
}
