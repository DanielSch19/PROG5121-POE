package com.example;
public class login {
    // User details for authentication
    private String username;
    private String password;
    private String phoneNum;

    public login(String username, String password, String phoneNum) {
        this.username = username;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    // Check if username meets requirements
    public static boolean checkUserName(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    // Check if password meets complexity requirements
    public static boolean checkPasswordComplexity(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[^a-zA-Z0-9].*");
    }

    // Check if phone number matches required format
    public static boolean checkCellPhoneNumber(String phoneNum) {
        return phoneNum.matches("^(\\+27|0)\\d{9}$");
    }

    // Check if login details match registered details
    public boolean loginUser(String username, String password, String phoneNum) {
        return this.username.equals(username) && this.password.equals(password) && this.phoneNum.equals(phoneNum);
    }
}