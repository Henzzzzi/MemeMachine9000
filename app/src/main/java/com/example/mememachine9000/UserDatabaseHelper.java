package com.example.mememachine9000;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import java.util.ArrayList;


// SQLite helper for local user database

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "UserDatabaseHelper";
    private static final String TABLE_NAME = "USERS";
    private static final String COL1 = "UID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "PASSWORD";
    private static final String COL4 = "SALT";
    private static final String COL5 = "TYPE";

    public UserDatabaseHelper(Context context) { super(context, TABLE_NAME, null, 1 ); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create database and table if not yet created
        String createTable ="CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " INTEGER," + COL2 + " TEXT," +
                COL3 + " TEXT," + COL4 + " TEXT," + COL5 + " INTEGER)";
        db.execSQL(createTable);


        // Creates default admin user when database is created
        PasswordHasher passwordHasher = PasswordHasher.getInstance();
        byte[] salt = passwordHasher.generateSalt();
        String hashedPassword = passwordHasher.hash("Admin", salt);
        String saltStr = Base64.encodeToString(salt, Base64.DEFAULT);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, 0);
        contentValues.put(COL2, "Admin");
        contentValues.put(COL3, hashedPassword);
        contentValues.put(COL4, saltStr);
        contentValues.put(COL5, 2);
        db.insert(TABLE_NAME, null, contentValues);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    // Add user to database
    public Boolean addUser(int userId, String name, String password, String salt, int type){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL1, userId);
        contentValues.put(COL2, name);
        contentValues.put(COL3, password);;
        contentValues.put(COL4, salt);
        contentValues.put(COL5, type);
        System.out.println(TAG + "addDAta: adding user '" + name + "' to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1){
            System.out.println("Error while adding user to database");
            return false;
        }
        else {
            System.out.println("Added user '" + name + "' to database!");
            return true;
        }
    }


    // Get data-cursor (used only inside this class)
    private Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM  " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    // Searches for user with matching username and hashed password from database.
    public boolean login(String userName, String password){
        Cursor data = getData();

        UserManager userManager = UserManager.getInstance();
        PasswordHasher passwordHasher = PasswordHasher.getInstance();

        while(data.moveToNext()){
            // Username
            if(data.getString(2).equals(userName)){
                String saltStr = data.getString(4);
                // Convert salt stored in Base64 back to byte[]
                byte[] salt = Base64.decode(saltStr, Base64.DEFAULT);
                // Password
                String hashedPassword = passwordHasher.hash(password, salt);
                if(data.getString(3).equals(hashedPassword)){
                    System.out.println("User '" + userName +"' logged in!");
                    userManager.setCurrentUser(new User(userName, hashedPassword, salt, data.getInt(1), data.getInt(5)));
                    return true;
                }
            }
        }
        System.out.println("Login failed!");
        return false;
    }


    // Find user by userId
    public User findUserById(int idToSearch){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = getData();

        while(data.moveToNext()) {
            if (data.getString(1).equals(String.valueOf(idToSearch))) {
                String saltStr = data.getString(4);
                byte[] salt = Base64.decode(saltStr, Base64.DEFAULT);

                User foundUser = new User(data.getString(2), data.getString(3), salt, idToSearch, data.getInt(5));
                return foundUser;
            }
        }
        return null;
    }


    // Get ArrayList of users. Used for editing users in manageAccountsActivity
    public ArrayList<User> getUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = getData();
        ArrayList <User> users = new ArrayList<User>();

        while(data.moveToNext()) {
            String saltStr = data.getString(4);
            byte[] salt = Base64.decode(saltStr, Base64.DEFAULT);

            User user = new User(data.getString(2), data.getString(3), salt, data.getInt(1), data.getInt(5));
            users.add(user);
        }
        return users;
    }


    // Find users databaseId by userId
    public int findUserDatabaseId(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = getData();

        while(data.moveToNext()) {
            if (data.getString(1).equals(String.valueOf(userId))) {
                return data.getInt(0);
            }
        }
        return -1;
    }


    // Edit user in database by userId
    public boolean editUser(int userId, String newUsername, String newPassword, int newType){

        int databaseId = findUserDatabaseId(userId);
        User currentUserInDatabase = findUserById(userId);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Update values which are not null
        // UserId and salt stays the same
        contentValues.put(COL1, userId);

        if(newUsername != null) {
            contentValues.put(COL2, newUsername);
        }
        else{
            contentValues.put(COL2, currentUserInDatabase.getUsername());
        }

        if(newPassword != null){
            contentValues.put(COL3, newPassword);
        }
        else{
            contentValues.put(COL3, currentUserInDatabase.getPassword());
        }

        String saltStr = Base64.encodeToString(currentUserInDatabase.getSalt(), Base64.DEFAULT);
        contentValues.put(COL4, saltStr);

        if(newType != -1){
            contentValues.put(COL5, newType);
        }
        else{
            contentValues.put(COL5, currentUserInDatabase.getType());
        }

        System.out.println("Editing user with id '" + userId + "' in database");

        long result = db.update(TABLE_NAME, contentValues, "ID=" + databaseId, null);
        if (result == -1){
            System.out.println("Error while editing user in database");
            return false;
        }
        else {
            System.out.println("Edited user with id '" + userId + "' in database!");
            return true;
        }
    }


    // Delete user from database
    public boolean deleteUser(int userId){

        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, COL1 + "=" + userId, null);
        if (result == -1){
            System.out.println("Error while removing user from database");
            return false;
        }
        else {
            System.out.println("Removed user with id '" + userId + "' from database!");
            return true;
        }
    }


    // Get number of users in database. Used to determine new user id
    public int getNumberOfUsers(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        int countInt = (int) count;
        return countInt;
    }


}

