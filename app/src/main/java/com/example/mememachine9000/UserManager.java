package com.example.mememachine9000;

import android.util.Base64;
import java.util.ArrayList;


// Used for creating new users / editing existing ones. Stores current User

public class UserManager {

    // Logged in user
    private User currentUser = null;

    // Singleton
    private static UserManager instance = null;

    private UserManager() { };

    static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    // Adds new user to database
    public void createNewUser(String userName, String password, UserDatabaseHelper udbHelper) {
        int newId = 0;
        int type = 1;    // Default user

        PasswordHasher passwordHasher = PasswordHasher.getInstance();

        newId = udbHelper.getNumberOfUsers();

        byte[] salt = passwordHasher.generateSalt();
        String hashedPassword = passwordHasher.hash(password, salt);

        String saltStr = Base64.encodeToString(salt, Base64.DEFAULT);

        udbHelper.addUser(newId, userName, hashedPassword, saltStr, type);
    }


    // Changes user's password in database
    public boolean changePassword(int userId, String newHashedPassword, UserDatabaseHelper udbHelper){
        udbHelper.editUser(userId, null, newHashedPassword, -1);
        return true;
    }


    // Generates ArrayList <String> containing user's uid and username. Used for ManageUsers
    public ArrayList <String> generateUserInfo(ArrayList <User> users){
        ArrayList <String> userInfo = new ArrayList<String>();
        String uInfo = "";

        userInfo.add("Coose a user to edit");

        for(int i = 0; i < users.size(); i++){
            uInfo = "UID: {" + users.get(i).getId() + "} , USERNAME: {" + users.get(i).getUsername() + "}";
            userInfo.add(uInfo);
        }

        return userInfo;
    }


    public void setCurrentUser(User newUser){
        currentUser = newUser;
    }


    public User getCurrentUser() {
        return currentUser;
    }


    public void currentUserLogout(){
        currentUser = null;
    }


}