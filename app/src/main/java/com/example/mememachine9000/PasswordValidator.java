package com.example.mememachine9000;

import java.util.regex.Pattern;


// Used for checking that new password is strong enough

public class PasswordValidator {

    // Singleton
    private static PasswordValidator instance = null;
    private PasswordValidator(){};

    static PasswordValidator getInstance(){
        if (instance == null){
            instance = new PasswordValidator();
        }
        return instance;
    }

    // List of special characters
    private final String CHARACTERS = "@#$%^&+=€/()_";
    // Patterns for validating password
    private final String UPPERCASE_PATTERN = "(?=.*[A-Z]).*";
    private final String LOWERCASE_PATTERN = "(?=.*[a-z]).*";
    private final String NUMBER_PATTERN = "(?=.*[0-9]).*";
    private final String SPECIAL_CHARACTER_PATTERN = "(?=.*[" + CHARACTERS + "]).*";

    private String isValidReturnMessage = "Stronk password!";


    // Checks if new password is strong enough and returns a string with not already met conditions
    public String validate (String password){

        String errorMessageLength = "";
        String errorMessageComposition = "";
        String errorMessageFinal = "";

        if (password.length() < 12){
            errorMessageLength = "Password should be at least 12 characters long\n";
        }
        if(!Pattern.compile(UPPERCASE_PATTERN).matcher(password).matches()){
            errorMessageComposition = errorMessageComposition.concat("... Uppercase letter\n");
        }
        if(!Pattern.compile(LOWERCASE_PATTERN).matcher(password).matches()){
            errorMessageComposition = errorMessageComposition.concat("... Lowercase letter\n");
        }
        if(!Pattern.compile(NUMBER_PATTERN).matcher(password).matches()){
            errorMessageComposition = errorMessageComposition.concat("... Number\n");
        }
        if(!Pattern.compile(SPECIAL_CHARACTER_PATTERN).matcher(password).matches()){
            errorMessageComposition = errorMessageComposition.concat("... Of these '" + CHARACTERS +"' special characters\n");
        }

        errorMessageFinal = errorMessageFinal.concat(errorMessageLength);
        // If some pattern condition is not met add generic prefix to message
        if(errorMessageComposition.length() != 0){
            errorMessageFinal = errorMessageFinal.concat("Password should include at least one...\n");
            errorMessageFinal = errorMessageFinal.concat(errorMessageComposition);
        }

        if(errorMessageFinal.length() != 0){
            return errorMessageFinal;
        }
        else{
            return isValidReturnMessage;
        }
    }


    // Returns true if password fulfills all requirements
    public boolean isValid (String password){
        if (validate(password).equals(isValidReturnMessage)){
            return true;
        }
        else {
            return false;
        }
    }

}
