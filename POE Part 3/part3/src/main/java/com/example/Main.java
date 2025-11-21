package com.example;

import java.util.Scanner;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Main {
      public static void main(String[] args) {
          Scanner scanner = new Scanner(System.in);
          final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");
    // Registration Section 
    // Collect initial registration details from the terminal.
    // We validate username, password and phone number using the Login helper methods.
    // If validation fails we exit early; otherwise we construct a Login instance for later use.
        System.out.println("******Registration******");
        System.out.print("Enter a user name (e.g., User_1): ");
        String username = scanner.nextLine();
        System.out.print("Enter a password (e.g., Password@123): ");
        String password = scanner.nextLine();
        System.out.print("Enter a phone number (e.g., +27123456789): ");
        String phoneNum = scanner.nextLine();

        // Validate registration inputs; return early on failure for clarity
        if (!Login.checkUserName(username)) {
            System.out.println("Username is not formatted correctly - ensure that your username contains an underscore and is no longer than 5 characters in length");
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

        //Login Section
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

        // ---------- Message sending functionality (main menu)
        // This loop shows three options: send messages, show sent messages, or exit.
        // Choosing option 1 prompts for how many messages to send, then collects each message.
        // The user may type 'menu' at the recipient or message prompt to return to the main menu.
        System.out.println("\nWelcome to QuickChat.");
        // minimize the window to view the JOptionPane with the message details

        int messageLimit = 0; // stored when user chooses option 1
        boolean running = true;
        while (running) {
            String menu = "Choose an option:\n1) Send message\n2) Show sent messages\n3) Display sender & recipient of sent messages\n4) Show longest sent message\n5) Search message by ID\n6) Search messages by recipient\n7) Delete message by hash\n8) Load stored messages from file\n9) Show stored messages\n10) Sent messages report\n11) Exit";
            String choiceLine = JOptionPane.showInputDialog(null, menu, "QuickChat - Menu", JOptionPane.QUESTION_MESSAGE);
            if (choiceLine == null) { // user closed/cancelled -> exit
                JOptionPane.showMessageDialog(null, "Goodbye.");
                break;
            }
            choiceLine = choiceLine.trim();
            if (!DIGITS_PATTERN.matcher(choiceLine).matches()) {
                JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2 or 3.");
                continue;
            }
            int choice = Integer.parseInt(choiceLine);

            if (choice == 1) {
                String limitLine = JOptionPane.showInputDialog(null, "How many messages would you like to send?", "Message Limit", JOptionPane.QUESTION_MESSAGE);
                if (limitLine == null) continue;
                limitLine = limitLine.trim();
                if (!DIGITS_PATTERN.matcher(limitLine).matches()) {
                    JOptionPane.showMessageDialog(null, "Invalid number. Please enter a valid integer.");
                    continue;
                }
                messageLimit = Integer.parseInt(limitLine);
                if (messageLimit < 1) {
                    JOptionPane.showMessageDialog(null, "Please enter a positive number.");
                    continue;
                }

                boolean returnToMenu = false;
                for (int i = 1; i <= messageLimit && !returnToMenu; i++) {
                    String header = "Message " + i + " of " + messageLimit + "\n(Type Cancel to return to the main menu)";
                    String recipient = JOptionPane.showInputDialog(null, header + "\nRecipient (+27... or 0...):", "Recipient", JOptionPane.QUESTION_MESSAGE);
                    if (recipient == null) {
                        returnToMenu = true;
                        continue;
                    }
                    recipient = recipient.trim();

                    String text = JOptionPane.showInputDialog(null, "Enter message text:", "Message Text", JOptionPane.QUESTION_MESSAGE);
                    if (text == null) {
                        returnToMenu = true;
                        continue;
                    }

                    // include sender username when creating messages
                    Message msg = new Message(text, recipient, username);
                    if (msg.checkRecipientCell() == 0) {
                        JOptionPane.showMessageDialog(null, "Invalid recipient format. Skipping this message.");
                        continue;
                    }

                    String actionPrompt = "Choose an option for this message:\n1) Send message\n2) Store message\n3) Disregard message";
                    String actionLine = JOptionPane.showInputDialog(null, actionPrompt, "Message Action", JOptionPane.QUESTION_MESSAGE);
                    if (actionLine == null) continue;
                    actionLine = actionLine.trim();
                    if (!DIGITS_PATTERN.matcher(actionLine).matches()) {
                        JOptionPane.showMessageDialog(null, "Invalid option!");
                        continue;
                    }
                    int action = Integer.parseInt(actionLine);
                    String result = msg.processChoice(action);
                    if (action == 1 || action == 2) {
                        String title = action == 1 ? "Message Sent" : "Message Stored";
                        JOptionPane.showMessageDialog(null, msg.getDetailsString(), title, JOptionPane.INFORMATION_MESSAGE);
                    }
                    JOptionPane.showMessageDialog(null, "Result: " + result);
                }

                JOptionPane.showMessageDialog(null, "Total messages sent this session: " + Message.returnTotalMessages());
            } else if (choice == 2) {
                JOptionPane.showMessageDialog(null, Message.printMessages(), "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 3) {
                JOptionPane.showMessageDialog(null, Message.displaySenderAndRecipient(), "Senders & Recipients", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 4) {
                JOptionPane.showMessageDialog(null, Message.getLongestSentMessageDetails(), "Longest Sent Message", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 5) {
                String id = JOptionPane.showInputDialog(null, "Enter Message ID to search:", "Search by ID", JOptionPane.QUESTION_MESSAGE);
                if (id != null) {
                    Message found = Message.findByMessageID(id.trim());
                    if (found != null) {
                        JOptionPane.showMessageDialog(null, found.getDetailsString(), "Message Found", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "No message found with ID: " + id);
                    }
                }
            } else if (choice == 6) {
                String recip = JOptionPane.showInputDialog(null, "Enter recipient phone number to search:", "Search by Recipient", JOptionPane.QUESTION_MESSAGE);
                if (recip != null) {
                    var list = Message.findByRecipient(recip.trim());
                    if (list.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No messages found for recipient: " + recip);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (Message m : list) sb.append(m.getDetailsString()).append("\n---\n");
                        JOptionPane.showMessageDialog(null, sb.toString(), "Messages for " + recip, JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else if (choice == 7) {
                String hash = JOptionPane.showInputDialog(null, "Enter message hash to delete:", "Delete by Hash", JOptionPane.QUESTION_MESSAGE);
                if (hash != null) {
                    boolean removed = Message.deleteByHash(hash.trim());
                    JOptionPane.showMessageDialog(null, removed ? "Message deleted." : "No message matched that hash.");
                }
            } else if (choice == 8) {
                try {
                    Message.loadStoredMessagesFromFile();
                    JOptionPane.showMessageDialog(null, "Stored messages loaded (if file existed).");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Failed to load stored messages: " + e.getMessage());
                }
            } else if (choice == 9) {
                var stored = Message.getStoredMessages();
                if (stored.isEmpty()) JOptionPane.showMessageDialog(null, "No stored messages.");
                else {
                    StringBuilder sb = new StringBuilder();
                    for (Message m : stored) sb.append(m.getDetailsString()).append("\n---\n");
                    JOptionPane.showMessageDialog(null, sb.toString(), "Stored Messages", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (choice == 10) {
                JOptionPane.showMessageDialog(null, Message.sentMessagesReport(), "Sent Messages Report", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice == 11) {
                JOptionPane.showMessageDialog(null, "Goodbye.");
                running = false;
                scanner.close();
                return;
            } else {
                JOptionPane.showMessageDialog(null, "Unknown option. Please choose 1, 2 or 3.");
            }
        }
        //When send the message minimize the window to view the JOptionPane with the message details
        scanner.close();
    }
}

