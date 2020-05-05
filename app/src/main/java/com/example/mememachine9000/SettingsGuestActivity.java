package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


// Settings activity for guest user

public class SettingsGuestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_guest);

        Switch darkModeEnabled = findViewById(R.id.switchDarkMode);
        final SessionManager sessionManager = SessionManager.getInstance();

        // Set initial state of the switch
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            darkModeEnabled.setChecked(true);
        }

        // Listener for theme switch
        darkModeEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sessionManager.saveTheme(getApplicationContext(), AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sessionManager.saveTheme(getApplicationContext(), AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }

    // Called when 'logout' button is clicked
    public void logout(View v){
        // Clear info about user from sharedPreferences
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.clearPreferences(this);

        // Set current user in userManager to null
        UserManager userManager = UserManager.getInstance();
        userManager.currentUserLogout();

        // Return lo loginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // Close all previous activities
        finish();
    }

}
