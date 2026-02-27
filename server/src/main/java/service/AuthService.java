package service;

import dataaccess.authDAO.AuthDAO;
import model.request.ClearRequest;
import model.result.ClearResult;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public ClearResult clear(ClearRequest clearRequest) {
        authDAO.clear();
        return new ClearResult();
    }
}
