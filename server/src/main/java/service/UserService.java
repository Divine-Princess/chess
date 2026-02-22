package service;

import model.request.RegisterRequest;
import model.result.RegisterResult;
import server.AlreadyTakenException;
import server.BadRequestException;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, BadRequestException {
        return new RegisterResult(registerRequest.username(), "");
    }
}
