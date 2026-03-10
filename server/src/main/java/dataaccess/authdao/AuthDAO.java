package dataaccess.authdao;

import dataaccess.DataAccessException;
import model.data.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
