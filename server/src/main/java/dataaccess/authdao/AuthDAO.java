package dataaccess.authdao;

import model.data.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clear();
}
