package service;

import dataaccess.authDAO.AuthDAO;
import dataaccess.userDAO.UserDAO;
import model.data.AuthData;
import model.data.UserData;
import model.request.ClearRequest;
import model.request.RegisterRequest;
import model.result.ClearResult;
import model.result.RegisterResult;
import server.AlreadyTakenException;
import server.BadRequestException;

import java.util.Objects;
import java.util.UUID;

public class UserService {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (Objects.equals(username, "") ||
                Objects.equals(password, "") ||
                Objects.equals(email, "")) {
            throw new BadRequestException("Error: Missing username, password, or email.");
        }


        UserData existingUser = userDAO.getUser(username);

        if (existingUser != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        UserData newUser = new UserData(username, password, email);

        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, newUser.username());

        authDAO.createAuth(newAuthData);

        return new RegisterResult(registerRequest.username(), authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public ClearResult clear(ClearRequest clearRequest) {
        userDAO.clear();

        return new ClearResult();
    }
}
