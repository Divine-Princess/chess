package service;

import dataaccess.authDAO.AuthDAO;
import dataaccess.userDAO.UserDAO;
import model.data.AuthData;
import model.data.UserData;
import model.request.ClearRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.result.ClearResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import server.AlreadyTakenException;
import server.BadRequestException;
import server.UnauthorizedException;

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

        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: Missing username, password, or email");
        }
        else if (username.isBlank() || password.isBlank() || email.isBlank()) {
            throw new BadRequestException("Error: Username, password, or email cannot be blank");
        }

        UserData existingUser = userDAO.getUser(username);

        if (existingUser != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }

        UserData newUser = new UserData(username, password, email);

        String authToken = createToken(newUser);

        userDAO.createUser(newUser);

        return new RegisterResult(registerRequest.username(), authToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public ClearResult clear(ClearRequest clearRequest) {
        userDAO.clear();

        return new ClearResult();
    }

    private String createToken(UserData user) {
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, user.username());

        authDAO.createAuth(newAuthData);

        return authToken;
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || password == null) {
            throw new BadRequestException("Error: Missing username or password");
        }
        else if (username.isBlank() || password.isBlank()) {
            throw new BadRequestException("Error: Missing username or password");
        }

        UserData user = userDAO.getUser(username);

        if (user == null) {
            throw new UnauthorizedException("Error: username or password is incorrect");
        }

        if (!Objects.equals(password, user.password())) {
            throw new UnauthorizedException("Error: username or password is incorrect");
        }

        String authToken = createToken(user);

        return new LoginResult(username, authToken);
    }

    @Override
    public String toString() {
        return "UserService{" +
                "userDAO=" + userDAO +
                ", authDAO=" + authDAO +
                '}';
    }
}

