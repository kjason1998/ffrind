package com.kevex.ffriend;

public class User {

    String email;
    String password;
    String username;
    String userID;
    String phoneNumber;

    public User() {

    }

    public User(String email, String password, String username, String phoneNumber) {
        setEmail(email);
        setPassword(password);
        setUsername(username);
        setPhoneNumber(phoneNumber);
    }

    public User(String userID, String email, String password, String username, String phoneNumber) {
        setUserID(userID);
        setEmail(email);
        setPassword(password);
        setUsername(username);
        setPhoneNumber(phoneNumber);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + this.getEmail() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", userID=" + this.getUserID() + '\'' +
                ", phoneNumber='" + this.getPhoneNumber() + '\'' +
                '}';
    }
}
