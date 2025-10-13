package com.example;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
      public static void main(String[] args) {
          Scanner scanner = new Scanner(System.in);
          final Pattern DIGITS = Pattern.compile("^\\d+$");
    // ---------- Registration Section ----------
    // Collect initial registration details from the terminal.
    // We validate username, password and phone number using the Login helper methods.
    // If validation fails we exit early; otherwise we construct a Login instance for later use.
        System.out.println("******Registration******");
        System.out.print("Enter a user name: ");
        String username = scanner.nextLine();
        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        System.out.print("Enter a phone number: ");
        String phoneNum = scanner.nextLine();

        // Validate registration inputs; return early on failure for clarity
        if (!Login.checkUserName(username)) {
            System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.");
            scanner.close();
            return;
        }
        System.out.println("Username successfully captured");

        if (!Login.checkPasswordComplexity(password)) {
            System.out.println("Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number and a special character.");
            scanner.close();
            return;
        }
        System.out.println("Password successfully captured");

        if (!Login.checkCellPhoneNumber(phoneNum)) {
            System.out.println("Phone number is not correctly formatted, please ensure that the phone number starts with +27 or 0 and is 10 digits long.");
            scanner.close();
            return;
        }
        System.out.println("Phone number successfully captured");
        Login user = new Login(username, password, phoneNum);
        System.out.println("Registration successful!");

        // ---------- Login Section ----------
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

        // ---------- Message sending functionality (main menu) ----------
        // This loop shows three options: send messages, show sent messages, or exit.
        // Choosing option 1 prompts for how many messages to send, then collects each message.
        // The user may type 'menu' at the recipient or message prompt to return to the main menu.
        System.out.println("\nWelcome to QuickChat.");

        int messageLimit = 0; // stored when user chooses option 1
        boolean running = true;
        while (running) {
            // Display menu and read a line to avoid Scanner.nextInt newline issues
            System.out.println();
            System.out.println("Choose an option:");
            System.out.println("1) Send message");
            System.out.println("2) Show sent messages");
            System.out.println("3) Exit");
            System.out.print("> ");
            String choiceLine = scanner.nextLine().trim();
            if (!DIGITS.matcher(choiceLine).matches()) {
                System.out.println("Invalid choice. Please enter 1, 2 or 3.");
                continue;
            }
            int choice = Integer.parseInt(choiceLine);

            if (choice == 1) {
                // Ask how many messages the user wants to send in this batch
                System.out.print("How many messages would you like to send? ");
                String limitLine = scanner.nextLine().trim();
                if (!DIGITS.matcher(limitLine).matches()) {
                    System.out.println("Invalid number. Please enter a valid integer.");
                    continue;
                }
                messageLimit = Integer.parseInt(limitLine);
                if (messageLimit < 1) {
                    System.out.println("Please enter a positive number.");
                    continue;
                }
                System.out.println("You may send up to " + messageLimit + " messages.");

                boolean returnToMenu = false;
                for (int i = 1; i <= messageLimit && !returnToMenu; i++) {
                    System.out.println("\nMessage " + i + " of " + messageLimit + ":");
                    System.out.println("(Type 'menu' at any prompt to return to the main menu)");
                    System.out.print("Recipient (+27... or 0...): ");
                    String recipient = scanner.nextLine().trim();
                    if (recipient.equalsIgnoreCase("menu")) {
                        System.out.println("Returning to main menu...");
                        returnToMenu = true;
                        continue;
                    }

                    // Collect message text, validate for 'menu' command as well
                    System.out.print("Enter message text: ");
                    String text = scanner.nextLine();
                    if (text.equalsIgnoreCase("menu")) {
                        System.out.println("Returning to main menu...");
                        returnToMenu = true;
                        continue;
                    }

                    // Create message object and validate recipient format
                    Message msg = new Message(text, recipient);
                    if (msg.checkRecipientCell() == 0) {
                        System.out.println("Invalid recipient format. Skipping this message.");
                        continue; // move to next message in the loop
                    }

                    // Let Message.sentMessage(...) handle send/store/disregard logic and UI display
                    String result = msg.sentMessage(scanner);
                    System.out.println("Result: " + result);
                }

                // After sending loop, display total messages sent so far in this run
                System.out.println("Total messages sent this session: " + Message.returnTotalMessages());
            } else if (choice == 2) {
                // Option 2: show messages accumulated during this run
                System.out.println(Message.printMessages());
            } else if (choice == 3) {
                // Exit the program gracefully
                System.out.println("Goodbye.");
                running = false;
                scanner.close();
                return;
            } else {
                System.out.println("Unknown option. Please choose 1, 2 or 3.");
            }
        }
        //When send the message minimize the window to view the JOptionPane with the message details
        scanner.close();
    }
}

