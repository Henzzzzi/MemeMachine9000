package com.example.mememachine9000;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


// adds salt and hashes password with sha-512

public class PasswordHasher {

    // Singleton
    private static PasswordHasher instance = null;
    private PasswordHasher(){};

    static PasswordHasher getInstance(){
        if (instance == null){
            instance = new PasswordHasher();
        }
        return instance;
    }

    // Hashes the provided password with sha-512 and provided salt
    public String hash (String password, byte[] salt){
        // Get digestor
        MessageDigest msgDigest = null;
        try{
            msgDigest = MessageDigest.getInstance("SHA-512");
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
        // Add salt and process password
        msgDigest.update(salt);
        byte[] byteArrayPassword = msgDigest.digest(password.getBytes());
        StringBuilder strBuilder = new StringBuilder();
        for (byte b: byteArrayPassword){
            strBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        String hashedPassword = strBuilder.toString();
        return hashedPassword;
    }

    // Generates a random salt
    public byte [] generateSalt (){
        SecureRandom randomizer = null;
        randomizer = new SecureRandom();

        byte[] salt = new byte[32];
        randomizer.nextBytes(salt);

        return salt;
    }

}