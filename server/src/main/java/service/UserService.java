package service;

import dataaccess.DataAccessException;
import dataaccess.authdao.AuthDAO;
import dataaccess.userdao.UserDAO;
import model.data.AuthData;
import model.data.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ClearResult;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;
import server.AlreadyTakenException;
import server.BadRequestException;
import server.UnauthorizedException;
import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException, DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (username == null || password == null || email == null) {
            throw new BadRequestException("Missing username, password, or email");
        }
        else if (username.isBlank() || password.isBlank() || email.isBlank()) {
            throw new BadRequestException("Username, password, or email cannot be blank");
        }

        UserData existingUser = userDAO.getUser(username);

        if (existingUser != null) {
            throw new AlreadyTakenException("username already taken");
        }

        String hashedPassword = hashPassword(password);

        UserData newUser = new UserData(username, hashedPassword, email);

        String authToken = createToken(newUser);

        userDAO.createUser(newUser);

        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || password == null) {
            throw new BadRequestException("Missing username or password");
        }
        else if (username.isBlank() || password.isBlank()) {
            throw new BadRequestException("Missing username or password");
        }

        UserData user = userDAO.getUser(username);

        if (user == null) {
            throw new UnauthorizedException("username or password is incorrect");
        }

        if (!verifyUser(user, password)) {
            throw new UnauthorizedException("username or password is incorrect");
        }

        String authToken = createToken(user);

        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        String authToken = logoutRequest.authToken();

        AuthData existingToken = authDAO.getAuth(authToken);

        if (existingToken == null) {
            throw new UnauthorizedException("unauthorized");
        }

        authDAO.deleteAuth(authToken);

        return new LogoutResult();
    }

    public ClearResult clear() throws DataAccessException {
        userDAO.clear();

        return new ClearResult();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String createToken(UserData user) throws DataAccessException {
        String authToken = generateToken();
        AuthData newAuthData = new AuthData(authToken, user.username());

        authDAO.createAuth(newAuthData);

        return authToken;
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    boolean verifyUser(UserData existingUser, String clearTextPassword) {

        String hashExistingPassword = existingUser.password();

        return BCrypt.checkpw(clearTextPassword, hashExistingPassword);
    }

    @Override
    public String toString() {
        return "UserService{" +
                "userDAO=" + userDAO +
                ", authDAO=" + authDAO +
                '}';
    }
}

