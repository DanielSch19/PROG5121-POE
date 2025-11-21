package com.example;
public class Login {
    // User details for authentication
    private String username;
    private String password;
    private String phoneNum;

    public Login(String username, String password, String phoneNum) {
        this.username = username;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    // Check if username meets requirements
    public static boolean checkUserName(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    /**
     * Build a welcome string for a user. Kept in Login for simplicity so tests
     * can verify the exact expected output without depending on Main.
     *
     * Format: "Welcome <firstName>, <lastName> it is great to see you"
     */
    public static String formatWelcome(String firstName, String lastName) {
        return "Welcome " + firstName + ", " + lastName + " it is great to see you";
    }

    /**
     * Return the standard username validation error message used in the UI.
     */
    public static String formatUsernameError() {
        return "Username is not formatted correctly - ensure that your username contains an underscore and is no longer than 5 characters in length";
    }

    /**
     * Return the standardized message for successful password capture.
     */
    public static String formatPasswordSuccess() {
        return "password captured successfully";
    }

    /**
     * Return the standardized message for invalid password format.
     */
    public static String formatPasswordError() {
        return "Incorrect password format ensure password is at least 8 characters in length, contains a capital letter, a number and a special character";
    }

    /**
     * Return standardized messages for phone validation.
     */
    public static String formatPhoneSuccess() {
        return "Phone number captured successfully";
    }

    public static String formatPhoneError() {
        return "Phone number is not correctly formatted, please ensure that the phone number starts with +27 or 0 and is 10 digits long.";
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