package com.example.mememachine9000;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;


// Used for managing SharedPreferences

public class SessionManager {

    // Singleton
    private static SessionManager instance = null;
    private SessionManager(){};

    static SessionManager getInstance(){
        if (instance == null){
            instance = new SessionManager();
        }
        return instance;
    }


    // Save new username and uid. Called from login if 'remember me' is checked
    public void saveUser (Context context, String username, int uid){
        SharedPreferences preferences = null;
        SharedPreferences.Editor editor;

        preferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        editor.putString("USERNAME", username);
        editor.putInt("UID", uid);
        editor.apply();
    }


    // Get saved uid. Called when app starts to check if there's a saved user
    public int getUid (Context context){
        SharedPreferences preferences = null;
        preferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);

        int uid = preferences.getInt("UID", -1);

        return uid;
    }


    public void saveTheme(Context context, int theme){
        SharedPreferences preferences = null;
        SharedPreferences.Editor editor;

        preferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        editor.putInt("THEME", theme);
        editor.apply();
    }


    // Get saved uid. Called when app starts to check if there's a saved user
    public int getTheme (Context context){
        SharedPreferences preferences = null;
        preferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);

        int theme = preferences.getInt("THEME", -77);

        return theme;
    }


    // Clears saved preferences. Used for logout
    public void clearPreferences (Context context){
        SharedPreferences preferences = null;
        SharedPreferences.Editor editor;

        preferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        editor.putString("USERNAME", "");
        editor.putInt("UID", -1);
        editor.putInt("THEME", -77);
        editor.apply();
    }

}
