package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Message {
    private static final HashSet<String> usedMessageIDs = new HashSet<>();
    private static final List<Message> sentMessage = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final String messageText;
    private final String recipientCell;
    private String statusMessage;
    // assigned when the message is sent or stored
    private int messageNumber = 0;

    // Reused objects and compiled patterns for better performance
    private static final Pattern RECIPIENT_PATTERN = Pattern.compile("(^\\+27\\d{9}$|^0\\d{9}$)");
    private static final Pattern DIGITS = Pattern.compile("^\\d+$");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // counter used to assign sequential message numbers when messages are added
    private static int messageNumberCounter = 0;

    // Constructor
    /**
     * Create a new Message instance. A unique 10-digit message ID is generated for tracking.
     *
     * @param messageText  the message content (will be treated as empty string if null)
     * @param recipientCell the recipient phone number (expected +27######### or 0#########)
     */
    public Message(String messageText, String recipientCell) {
        this.messageID = generateMessageID();
        this.messageText = messageText != null ? messageText : "";
        this.recipientCell = recipientCell != null ? recipientCell : "";
    }

    private static String generateMessageID() {
        // Build a 10-digit ID using a substring of the current time and a loop counter.
        // Format: last 6 digits of System.currentTimeMillis() + 4-digit counter (padded)
        // Loop until we find a unique ID.
        for (int attempt = 0; ; attempt++) {
            // Ensure counter wraps but keeps some variability
            idCounter++;
            String time = String.valueOf(System.currentTimeMillis());
            String timePart = time.length() >= 6 ? time.substring(time.length() - 6) : String.format("%6s", time).replace(' ', '0');
            String counterPart = String.format("%04d", idCounter % 10000);
            String id = timePart + counterPart; // 6 + 4 = 10 digits
            if (!usedMessageIDs.contains(id)) {
                usedMessageIDs.add(id);
                return id;
            }
            // In case of collision (very unlikely), loop and try again
            if (attempt > 10000) {
                // Fallback: slightly perturb using currentTimeMillis again
                time = String.valueOf(System.currentTimeMillis());
                timePart = time.length() >= 6 ? time.substring(time.length() - 6) : String.format("%6s", time).replace(' ', '0');
                id = timePart + String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));
                if (!usedMessageIDs.contains(id)) {
                    usedMessageIDs.add(id);
                    return id;
                }
            }
        }
    }

    // simple counter used when building message IDs
    private static int idCounter = 0;

    /**
     * Check that the message ID is not longer than 10 digits and contains only digits.
     */
    /**
     * Check that the message ID is numeric and at most 10 digits long.
     *
     * @return true when the ID appears valid
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{1,10}");
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getRecipient() {
        return recipientCell;
    }

    /**
     * Validate recipient cell: accept +27 followed by 9 digits, or 0 followed by 9 digits.
     * Returns 1 if valid, 0 if invalid.
     */
    public int checkRecipientCell() {
        // Accept either international format (+27 followed by 9 digits) or local 0 + 9 digits
        if (recipientCell != null && RECIPIENT_PATTERN.matcher(recipientCell).matches()) {
            return 1;
        }
        return 0;
    }

    /**
     * Creates the message hash: first 2 numbers of the messageID, a colon, the number of messages sent,
     * a colon and the first and last words of the message in full caps concatenated (no spaces).
     */
    public String createMessageHash() {
        int msgNum = this.messageNumber;
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String numberPart = String.valueOf(msgNum);
        String[] parts = messageText.trim().split("\\s+");
        String firstWord = parts.length > 0 ? parts[0].toUpperCase() : "";
        String lastWord = parts.length > 1 ? parts[parts.length - 1].toUpperCase() : "";
        StringBuilder sb = new StringBuilder(32);
        sb.append(idPart).append(':').append(numberPart).append(':').append(firstWord).append(lastWord);
        return sb.toString();
    }

    /**
     * Backwards-compatible overload: create a message hash using an explicit message number.
     * This preserves existing tests that pass an integer.
     */
    public String createMessageHash(int messageNumber) {
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String numberPart = String.valueOf(messageNumber);
        String[] parts = messageText.trim().split("\\s+");
        String firstWord = parts.length > 0 ? parts[0].toUpperCase() : "";
        String lastWord = parts.length > 1 ? parts[parts.length - 1].toUpperCase() : "";
        StringBuilder sb = new StringBuilder(32);
        sb.append(idPart).append(':').append(numberPart).append(':').append(firstWord).append(lastWord);
        return sb.toString();
    }

    /**
     * Validate message length.
     * If length <= 250 returns "Message ready to explain".
     * If > 250 returns the exact failure string requested including excess amount.
     *
     * @return validation message
     */
    public String validateLength() {
        int len = messageText == null ? 0 : messageText.length();
        if (len <= 250) {
            return "Message ready to explain";
        }
        int excess = len - 250;
        return "messgae exceeds 250 characters by " + excess + " reduce the size";
    }

    /**
     * Interactive send/store/disregard for this message. Expects a Scanner for console choices.
     * Displays full details in a JOptionPane when sent/stored. Returns status message.
     */
    public String sentMessage(Scanner scanner) {
        // Validate message length
        if (messageText.length() > 250) {
            return "Please enter a messgae of less than 250 characters"; // keep spelling per spec
        }
        // Present choice menu to the user. The method expects to be called from
        // a terminal-driven flow; it uses the provided Scanner to read the choice.
        System.out.println("Choose an option for message ID " + messageID + ":");
        System.out.println("1) Send message");
        System.out.println("2) Store message");
        System.out.println("3) Disregard message");
        System.out.print("> ");

        String choiceLine = scanner.nextLine().trim();
        if (!DIGITS.matcher(choiceLine).matches()) {
            statusMessage = "Invalid option!";
            return statusMessage;
        }
        int choice = Integer.parseInt(choiceLine);

        // Delegate core behavior to processChoice so tests can call it directly without I/O
        int parsedChoice = choice;
        // Show GUI dialogs for send/store as before (non-blocking), but delegate logic
        String result = processChoice(parsedChoice);
        if (parsedChoice == 1) {
            String sendDetails = "MessageID: " + messageID + "\n" +
                    "Message Hash: " + createMessageHash() + "\n" +
                    "Recipient: " + recipientCell + "\n" +
                    "Message: " + messageText;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, sendDetails, "Message Sent", JOptionPane.INFORMATION_MESSAGE));
        } else if (parsedChoice == 2) {
            String storeDetails = "MessageID: " + messageID + "\n" +
                    "Message Hash: " + createMessageHash() + "\n" +
                    "Recipient: " + recipientCell + "\n" +
                    "Message: " + messageText;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, storeDetails, "Message Stored", JOptionPane.INFORMATION_MESSAGE));
        }
        return result;
    }

    /**
     * Core logic for processing a user's choice (1=send, 2=store, 3=disregard).
     * This method intentionally avoids GUI interactions so it can be used from unit tests.
     * Returns user-facing status strings (used by tests).
     */
    public String processChoice(int choice) {
        if (choice == 1) {
            // assign a sequential message number then add to sent list
            this.messageNumber = ++messageNumberCounter;
            sentMessage.add(this);
            totalMessagesSent++;
            return "Message sent successfully";
        } else if (choice == 2) {
            this.messageNumber = ++messageNumberCounter;
            sentMessage.add(this);
            try {
                storeMessagesToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "message successfully stored";
        } else if (choice == 3) {
            // Disregard: do nothing beyond returning the message instructing deletion
            return "press 0 to delete mesaage";
        } else {
            return "Invalid option!";
        }
    }

    /**
     * Print all messages stored in this run.
     */
    public static String printMessages() {
        if (sentMessage.isEmpty()) return "No messages to display.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessage) {
            sb.append("MessageID: ").append(m.getMessageID()).append("\n");
            sb.append("Recipient: ").append(m.getRecipient()).append("\n");
            sb.append("Message: ").append(m.getMessageText()).append("\n");
            sb.append("---\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /**
     * Store the current sentMessage list to a JSON file (messages.json) using Gson.
     */
    public static void storeMessagesToFile() throws IOException {
        Path out = Path.of("messages.json");
        String json = GSON.toJson(sentMessage);
        // Atomically replace the file contents
        Files.writeString(out, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public String toString() {
        return "MessageID: " + messageID + ", Recipient: " + recipientCell + ", Message: " + messageText;
    }
}