package com.kevex.ffriend.Model;

import java.io.Serializable;

public class User implements Serializable {

    String email;
    String username;
    String userID;
    String phonenumber;
    String avatarUrl;
    String bio;
    String gender;
    String age;
    double lat;
    double lon;
    double points;



    /**
     * TODO: Make sure to check set methods
     * A User represent one user
     */
    public User() {

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

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", userID='" + userID + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
