package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private final ArrayList<UserData> userDataList = new ArrayList<>();


    public MemoryUserDAO() {
    }


    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if((username == null || username.isEmpty()) || (password == null || password.isEmpty())) {
            throw new DataAccessException("Error: bad request", 400);
        }
        for (UserData temp : this.userDataList) {
            if (temp.username().equals(username)) {
                if(temp.password().equals(password)) {
                    return temp;
                }
            }
        }
        throw new DataAccessException("Error: unauthorized", 401);
    }

    @Override
    public void clearAllUserData() throws DataAccessException {
        try {
            userDataList.clear();
        } catch (Error e) {
            throw new DataAccessException("Error: could not clear database", 500);
        }
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        try {
            UserData user = getUser(userData.username(), userData.password());
            if(user != null) {
                throw new DataAccessException("Error: already taken", 403);
            }
        } catch (DataAccessException e) {
            if(e.statusCode() == 401) {
                this.userDataList.add(userData);
                return userData;
            }
            if(e.statusCode() == 403) {
                throw e;
            }
            if(e.statusCode() == 400) {
                throw e;
            }
        }
        throw new DataAccessException("Error: Could not create user", 500);
    }

    @Override
    public String toString() {
        StringBuilder daoString = new StringBuilder();
        for(UserData userDataString : userDataList) {
            daoString.append(userDataString.toString());
            daoString.append("\n");
        }
        return daoString.toString();
    }
}
