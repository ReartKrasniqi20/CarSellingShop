package com.example.carsellingshop.Model;

public class User {
    private String uid;
    private String username;
    private String email;
    private String userType; // "normal" or "admin"

    // Required empty constructor for Firestore
    public User() {}

    public User(String uid, String username, String email, String userType) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.userType = userType;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}