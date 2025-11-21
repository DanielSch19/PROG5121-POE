package com.example;

import java.util.Scanner;



public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Registration Section
        System.out.println("******Registration******");
        System.out.print("Enter a user name: ");
        String username = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Enter a phone number: ");
        String phoneNum = scanner.nextLine();

        if (!login.checkUserName(username)) {
            System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.");
            System.exit(0);
            } else {
                System.out.println("Username successfully captured");
                if (!login.checkPasswordComplexity(password)) {
                    System.out.println("Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number and a special character.");
                    System.exit(0);
                } 
                else {
                    System.out.println("Password successfully captured");
                    if (!login.checkCellPhoneNumber(phoneNum)) {
                        System.out.println("Phone number is not correctly formatted, please ensure that the phone number starts with +27 or 0 and is 10 digits long.");
                        System.exit(0);
                    } 
                    else {
                        System.out.println("Phone number successfully captured");
                        login user = new login(username, password, phoneNum);
                        System.out.println("Registration successful!");
                        
    
            // Login Section
            System.out.println("******Login******");
            System.out.print("Enter your Username: ");
            String loginUsername = scanner.nextLine();
            System.out.print("Enter your password: ");
            String loginPassword = scanner.nextLine();
            System.out.print("Enter your phone number: ");
            String loginPhoneNum = scanner.nextLine();

            if(user.loginUser(loginUsername, loginPassword, loginPhoneNum)){
                System.out.println("Login successful! ");
            } else {
                System.out.println("Login failed! Please check your username, password, and phone number.");
        }

        System.out.println("Enter your first name: ");
        String fName = scanner.nextLine();
        System.out.println("Enter your last name: ");
        String lName = scanner.nextLine();
        System.out.println("Welcome " + fName + " " + lName + " it's great to see you again.");
        }

   
        scanner.close();
    }
}
}
}