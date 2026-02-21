package dataaccess.userDAO;

import model.data.UserData;

public interface UserDAO {
    UserData getUser(String username);

    void createUser(UserData userData);

    void deleteUser(String username);


}
