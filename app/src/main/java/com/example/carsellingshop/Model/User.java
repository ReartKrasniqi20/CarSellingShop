package com.example.carsellingshop.Model;

public class User {
    private int id;
    private String username;
    private String email;
    private String hashedPassword;
    private String userType; // "normal" or "admin"

    public User(int id, String username, String email, String hashedPassword, String userType) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.userType = userType;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
