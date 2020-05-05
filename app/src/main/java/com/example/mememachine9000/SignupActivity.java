package com.example.mememachine9000;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


// User sing up - activity

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, passwordAgainEditText;
    private TextView passwordStatus, usernameStatus, passwordRequirements;
    private UserDatabaseHelper udpHelper = null;
    private UserManager userManager = null;
    private PasswordValidator passwordValidator = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordAgainEditText = findViewById(R.id.editTextPasswordAgain);

        usernameStatus = findViewById(R.id.usernameStatus);
        passwordStatus = findViewById(R.id.passwordStatus);
        passwordRequirements = findViewById(R.id.passwordRequirements);

        udpHelper = new UserDatabaseHelper(this);
        userManager = UserManager.getInstance();
        passwordValidator = PasswordValidator.getInstance();

        String initialPasswordValidationMessage = passwordValidator.validate("");
        passwordRequirements.setText(initialPasswordValidationMessage);
        passwordRequirements.setTextColor(Color.RED);


        // Displays a dynamic message about length of username
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(usernameEditText.getText().toString().length() < 5){
                    usernameStatus.setText("Username should be at least 5 characters long!");
                    usernameStatus.setTextColor(Color.RED);
                }
                else{
                    usernameStatus.setText("Username OK");
                    usernameStatus.setTextColor(Color.GREEN);
                }
            }
        });

        // Displays status messages 'passwords match' or 'passwords don't match'
        // Displays status messages of new password's content
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())){
                    passwordStatus.setText("Passwords do not match!");
                    passwordStatus.setTextColor(Color.RED);
                }
                else{
                    passwordStatus.setText("Passwords match!");
                    passwordStatus.setTextColor(Color.GREEN);
                }

                String passwordValidationMessage = passwordValidator.validate(passwordEditText.getText().toString());
                passwordRequirements.setText(passwordValidationMessage);
                if(passwordValidator.isValid(passwordEditText.getText().toString())){
                    passwordRequirements.setTextColor(Color.GREEN);
                }
                else{
                    passwordRequirements.setTextColor(Color.RED);
                }

            }
        });

        // Updates 'passwords match' or 'passwords don't match' when 'password again' is  edited
        passwordAgainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!passwordEditText.getText().toString().equals(passwordAgainEditText.getText().toString())){
                    passwordStatus.setText("Passwords do not match!");
                    passwordStatus.setTextColor(Color.RED);
                }
                else{
                    passwordStatus.setText("");
                }
            }
        });

    }

    // Called when 'sing up' button is clicked
    // Checks that username and password meets all requirements and creates new user to database
    public void signUp (View v){
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (userName.length() >= 5 && password.equals(passwordAgain) && passwordValidator.isValid(password)){
            userManager.createNewUser(userName, password, udpHelper);
            String msg = "User '" + userName + "' created!";
            Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(SignupActivity.this, "Please check your username and password!", Toast.LENGTH_LONG).show();
        }
    }
}
