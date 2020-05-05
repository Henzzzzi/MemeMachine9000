package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


// Activity used for login

public class LoginActivity extends AppCompatActivity implements AuthenticationDialog.InputListener{

    private EditText usernameEditText, passwordEditText;
    private CheckBox rememberBox;
    private UserDatabaseHelper udpHelper = null;
    private UserManager userManager = null;
    private SessionManager sessionManager = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rememberBox = findViewById(R.id.checkBoxRemember);
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);

        udpHelper = new UserDatabaseHelper(this);
        userManager = UserManager.getInstance();
        sessionManager = SessionManager.getInstance();

        // Check for saved theme from savedPreferences
        if(sessionManager.getTheme(this) != -77){
            AppCompatDelegate.setDefaultNightMode(sessionManager.getTheme(this));
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
        }

        // If there's saved user, log in and switch to MainActivity
        if(sessionManager.getUid(this) != -1) {

            User userFromDatabase = udpHelper.findUserById(sessionManager.getUid(this));

            if(userFromDatabase != null) {
                userManager.setCurrentUser(userFromDatabase);

                String loginSuccessMsg = "Welcome " + userManager.getCurrentUser().getUsername() + "!";
                Toast.makeText(LoginActivity.this, loginSuccessMsg, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                System.out.println("Cannot log in automatically with saved user id {" + sessionManager.getUid(this) +"}");
            }
        }
    }


    // Called when 'login' button is clicked
    public void login(View v){
        String loginUsername = usernameEditText.getText().toString();
        String loginPassword = passwordEditText.getText().toString();
        Boolean loginSuccess = false;

        loginSuccess = udpHelper.login(loginUsername, loginPassword);

        String loginFailMsg = "Login failed, invalid credentials!";

        if (loginSuccess){
            // Call two factor verification dialog
            // Login process continues in sendInput method below if verification succeeds
            AuthenticationDialog dialog = new AuthenticationDialog();
            dialog.show(getSupportFragmentManager(), "AuthenticationDialog");
        }
        else {
            Toast.makeText(LoginActivity.this, loginFailMsg, Toast.LENGTH_SHORT).show();
        }
    }


    // Interface for communicating with authentication dialog
    // Receive true if two factor verification has succeeded and continues login process
    @Override
    public void sendInput(boolean input) {
        if(input){
            User currentUser = userManager.getCurrentUser();
            if (rememberBox.isChecked()){
                sessionManager.saveUser(this, currentUser.getUsername(), currentUser.getId());
            }

            String loginSuccessMsg = "Welcome " + currentUser.getUsername() + "!";

            Toast.makeText(LoginActivity.this, loginSuccessMsg, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(LoginActivity.this, "Two factor verification failed, please try again", Toast.LENGTH_SHORT).show();
        }
    }


    // Called when 'singup' button is clicked
    public void openSingupActivity(View v){
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }


    // Called when 'continue as a guest' button is clicked
    public void continueAsGuest(View v){
        userManager.setCurrentUser(new User("Guest", "", new byte[32], -1, 0));
        System.out.println("Guest user logged in");
        Toast.makeText(LoginActivity.this, "Welcome Guest!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
