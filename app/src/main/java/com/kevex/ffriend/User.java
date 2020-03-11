package com.kevex.ffriend;

public class User {

    String email;
    String username;
    String userID;
    String phonenumber;
    String bio;
    double lat;
    double lon;

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User() {

    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public User(String email, String username, String phoneNumber) {
        setEmail(email);
        setUsername(username);
        setPhoneNumber(phoneNumber);
    }

    public User(String userID, String email, String username, String phoneNumber) {
        setUserID(userID);
        setEmail(email);
        setUsername(username);
        setPhoneNumber(phoneNumber);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return phonenumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phonenumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", userID='" + userID + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", bio='" + bio + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
