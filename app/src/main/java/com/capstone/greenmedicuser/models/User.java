package com.capstone.greenmedicuser.models;



public class User {

    private boolean isUserSignup;
    private String fullName;
    private String email;
    private String userAddress;
    private String username;
    private String password;
    private String geoLocation;

    public User() {
    }

    public User(boolean isUserSignup, String fullName, String email, String userAddress,
                String username, String password, String geoLocation) {
        this.isUserSignup = isUserSignup;
        this.fullName = fullName;
        this.email = email;
        this.userAddress = userAddress;
        this.username = username;
        this.password = password;
        this.geoLocation = geoLocation;
    }

    public boolean isUserSignup() {
        return isUserSignup;
    }

    public void setUserSignup(boolean userSignup) {
        isUserSignup = userSignup;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }
}
