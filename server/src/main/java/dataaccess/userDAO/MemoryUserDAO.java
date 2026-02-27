package dataaccess.userDAO;

import model.data.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> user;

    public MemoryUserDAO() {
        this.user = new HashMap<>();
    }

    @Override
    public void createUser(UserData userData) {
        user.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return user.get(username);
    }

    @Override
    public void deleteUser(String username) {
        user.remove(username);
    }

    @Override
    public void clear() {
        user.clear();
    }

    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "user=" + user +
                '}';
    }
}
