package dataaccess.userDAO;

import model.data.UserData;

public interface UserDAO {

    void createUser(UserData userData);

    UserData getUser(String username);

    void deleteUser(String username);


}
