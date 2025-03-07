package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private final ArrayList<UserData> userDataList = new ArrayList<>();


    public MemoryUserDAO() {
    }



    private void removeUserData(UserData o) {
        this.userDataList.remove(o);
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        for (UserData temp : this.userDataList) {
            if (temp.username().equals(username)) {
                if(temp.password().equals(password)) {
                    return temp;
                }
                throw new DataAccessException("Error: unauthorized", 401);
            }
        }
        throw new DataAccessException("Error: User not found", 500);
    }

    @Override
    public UserData getUser(String username, String password, String email) throws DataAccessException {
        return null;
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        try {
            UserData user = getUser(userData.username(), userData.password());
            if(user != null) {
                throw new DataAccessException("Error: already taken", 403);
            }
        } catch (DataAccessException e) {
            if(e.StatusCode() == 401) {
                throw new DataAccessException("Error: bad request", 400);
            }
            if(e.StatusCode() == 403) {
                throw e;
            }
            this.userDataList.add(userData);
            return userData;
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
