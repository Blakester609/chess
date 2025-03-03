package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Set;

public class MemoryUserDAO implements UserDAO {
    private final ArrayList<UserData> userDataList = new ArrayList<>();


    public MemoryUserDAO() {
    }



    private void removeUserData(UserData o) {
        this.userDataList.remove(o);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData temp : this.userDataList) {
            if (temp.username().equals(username)) {
                return temp;
            }
        }
        throw new DataAccessException("Error: User not found");
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if(getUser(userData.username()) == null) {
            this.userDataList.add(userData);
        } else {
            throw new DataAccessException("Error: Could not create user");
        }
    }
}
