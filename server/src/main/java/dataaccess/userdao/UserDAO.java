package dataaccess.userdao;

import model.data.UserData;

import java.sql.SQLException;

public interface UserDAO {

    void createUser(UserData userData) throws SQLException;

    UserData getUser(String username);

    void clear();
}
