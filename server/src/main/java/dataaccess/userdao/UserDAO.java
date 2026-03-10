package dataaccess.userdao;

import dataaccess.DataAccessException;
import model.data.UserData;

import java.sql.SQLException;

public interface UserDAO {

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
