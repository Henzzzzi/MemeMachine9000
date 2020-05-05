package com.example.mememachine9000;


// Class for storing user information

public class User {

        private int id = -1;
        private String username = "";
        private String password = "";
        private byte[] salt = null;
        private int type = -1;  // 0 - guest, 1 - normal, 2 - admin

        // Constructor
        public User(String new_username, String new_password, byte[] new_salt, int new_id, int new_type){
            username = new_username;
            password = new_password;
            salt = new_salt;
            id = new_id;
            type = new_type;
        }

        public String getUsername() {
            return username;
        }

        public  String getPassword() {
            return password;
        }

        public byte[] getSalt() {
            return salt;
        }

        public int getId() { return id; }

        public int getType() { return type; }

}
