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
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private final String messageText;
    private final String recipientCell;
    // optional sender name (may be unknown for messages created without a sender)
    private final String sender;
    private String statusMessage;
    // assigned when the message is sent or stored
    private int messageNumber = 0;

    // Reused objects and compiled patterns for better performance
    private static final Pattern RECIPIENT_PATTERN = Pattern.compile("(^\\+27\\d{9}$|^0\\d{9}$)");
    private static final Pattern DIGITS = Pattern.compile("^\\d+$");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // counter used to assign sequential message numbers when messages are added
    private static int messageCounter = 0;

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
        this.sender = "Unknown";
    }

    /**
     * Overloaded constructor allowing an explicit sender.
     */
    public Message(String messageText, String recipientCell, String sender) {
        this.messageID = generateMessageID();
        this.messageText = messageText != null ? messageText : "";
        this.recipientCell = recipientCell != null ? recipientCell : "";
        this.sender = sender != null ? sender : "Unknown";
    }

    /**
     * Constructor that accepts an explicit messageID (used when restoring from file).
     */
    public Message(String messageID, String messageText, String recipientCell, String sender, int messageNumber) {
        this.messageID = messageID != null ? messageID : generateMessageID();
        this.messageText = messageText != null ? messageText : "";
        this.recipientCell = recipientCell != null ? recipientCell : "";
        this.sender = sender != null ? sender : "Unknown";
        this.messageNumber = messageNumber;
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

    public String getSender() {
        return sender;
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
        return buildHash(this.messageNumber);
    }

    /**
     * Backwards-compatible overload: create a message hash using an explicit message number.
     * This preserves existing tests that pass an integer.
     */
    public String createMessageHash(int messageNumber) {
        return buildHash(messageNumber);
    }

    // Helper used by both public hash APIs to avoid duplicate logic
    private String buildHash(int number) {
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String numberPart = String.valueOf(number);
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
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, getDetailsString(), "Message Sent", JOptionPane.INFORMATION_MESSAGE));
        } else if (parsedChoice == 2) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, getDetailsString(), "Message Stored", JOptionPane.INFORMATION_MESSAGE));
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
            this.messageNumber = ++messageCounter;
            sentMessages.add(this);
            // track ids and hashes
            messageIDs.add(this.getMessageID());
            messageHashes.add(this.createMessageHash());
            totalMessagesSent++;
            return "Message sent successfully";
        } else if (choice == 2) {
            this.messageNumber = ++messageCounter;
            sentMessages.add(this);
            // stored messages keep a copy in storedMessages
            storedMessages.add(this);
            messageIDs.add(this.getMessageID());
            messageHashes.add(this.createMessageHash());
            try {
                storeMessagesToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "message successfully stored";
        } else if (choice == 3) {
            // Disregard: keep a record of the disregarded message
            disregardedMessages.add(this);
            return "press 0 to delete mesaage";
        } else {
            return "Invalid option!";
        }
    }

    // ===== New helper APIs for user requirements =====
    public static List<Message> getSentMessages() {
        return List.copyOf(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return List.copyOf(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        return List.copyOf(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return List.copyOf(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return List.copyOf(messageIDs);
    }

    /**
     * Display sender and recipient for all sent messages.
     */
    public static String displaySenderAndRecipient() {
        if (sentMessages.isEmpty()) return "No messages to display.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("Sender: ").append(m.getSender()).append(" | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Return the longest sent message (by text length) or null if none.
     */
    public static Message getLongestSentMessage() {
        Message longest = null;
        // consider both sent and stored messages for longest message calculation
        for (Message m : sentMessages) {
            if (longest == null || m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        for (Message m : storedMessages) {
            if (longest == null || m.getMessageText().length() > longest.getMessageText().length()) {
                longest = m;
            }
        }
        return longest;
    }

    public static String getLongestSentMessageDetails() {
        Message m = getLongestSentMessage();
        return m == null ? "No sent messages." : m.getDetailsString();
    }

    public static Message findByMessageID(String id) {
        for (Message m : sentMessages) {
            if (m.getMessageID().equals(id)) return m;
        }
        for (Message m : storedMessages) {
            if (m.getMessageID().equals(id)) return m;
        }
        return null;
    }

    public static List<Message> findByRecipient(String recipient) {
        List<Message> out = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.getRecipient().equals(recipient)) out.add(m);
        }
        for (Message m : storedMessages) {
            if (m.getRecipient().equals(recipient)) out.add(m);
        }
        return out;
    }

    /**
     * Delete a sent message by its hash. Returns true if removed.
     */
    public static boolean deleteByHash(String hash) {
        // Try to find the message in sentMessages first
        for (int i = 0; i < sentMessages.size(); i++) {
            Message m = sentMessages.get(i);
            if (m.createMessageHash().equals(hash)) {
                // remove from sentMessages
                sentMessages.remove(i);
                // also remove any stored copy with same identity or same messageID
                storedMessages.removeIf(sm -> sm == m || sm.getMessageID().equals(m.getMessageID()));
                // remove all occurrences of the message ID and the hash from tracking lists
                messageIDs.removeIf(id -> id.equals(m.getMessageID()));
                messageHashes.removeIf(h -> h.equals(hash));
                // record as disregarded
                disregardedMessages.add(m);
                // adjust total counter if appropriate
                if (totalMessagesSent > 0) totalMessagesSent--;
                return true;
            }
        }

        // If not found in sentMessages, try storedMessages (covers stored-only messages)
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.createMessageHash().equals(hash)) {
                storedMessages.remove(i);
                // ensure it's also removed from sentMessages if present
                sentMessages.removeIf(sm -> sm == m || sm.getMessageID().equals(m.getMessageID()));
                messageIDs.removeIf(id -> id.equals(m.getMessageID()));
                messageHashes.removeIf(h -> h.equals(hash));
                disregardedMessages.add(m);
                if (totalMessagesSent > 0) totalMessagesSent--;
                return true;
            }
        }

        return false;
    }

    /**
     * Display a detailed report for all sent messages.
     */
    public static String sentMessagesReport() {
        if (sentMessages.isEmpty()) return "No messages to display.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetailsString()).append("\nSender: ").append(m.getSender()).append("\n---\n");
        }
        return sb.toString();
    }

    /**
     * Load stored messages from `messages.json` into the `storedMessages` list.
     * This reads the JSON produced by `storeMessagesToFile()` and recreates
     * Message objects (sender will be Unknown if not present).
     */
    public static void loadStoredMessagesFromFile() throws IOException {
        // Prefer the module-local messages.json (part3/messages.json) when present,
        // otherwise fall back to a top-level messages.json.
        Path alt = Path.of("part3", "messages.json");
        Path in = Files.exists(alt) ? alt : Path.of("messages.json");
        if (!Files.exists(in)) return;
        String json = Files.readString(in);
        var je = GSON.fromJson(json, com.google.gson.JsonElement.class);
        if (je == null || !je.isJsonArray()) return;
        var arr = je.getAsJsonArray();
        System.out.println("[Message.load] entries in messages.json: " + arr.size());
        for (var elem : arr) {
            var obj = elem.getAsJsonObject();
            String text = obj.has("messageText") ? obj.get("messageText").getAsString() : "";
            String recip = obj.has("recipientCell") ? obj.get("recipientCell").getAsString() : "";
            String sender = obj.has("sender") ? obj.get("sender").getAsString() : "Unknown";
            String flag = obj.has("flag") ? obj.get("flag").getAsString().toLowerCase() : "";
            System.out.println("[Message.load] flag=" + flag + " recipient=" + recip + " text=" + text);
            if ("sent".equals(flag)) {
                String restoredId = null;
                if (recip != null && recip.matches("0\\d{9}")) {
                    restoredId = recip;
                }
                Message m;
                if (restoredId != null) {
                    m = new Message(restoredId, text, recip, sender, ++messageCounter);
                } else {
                    m = new Message(text, recip, sender);
                    m.messageNumber = ++messageCounter;
                }
                sentMessages.add(m);
                messageIDs.add(m.getMessageID());
                messageHashes.add(m.createMessageHash());
            } else if ("stored".equals(flag)) {
                Message m = new Message(text, recip, sender);
                m.messageNumber = ++messageCounter;
                storedMessages.add(m);
                messageIDs.add(m.getMessageID());
                messageHashes.add(m.createMessageHash());
            } else if ("disregard".equals(flag) || "disregarded".equals(flag)) {
                Message m = new Message(text, recip, sender);
                m.messageNumber = ++messageCounter;
                disregardedMessages.add(m);
            } else {
                // ignore entries without a recognized flag
            }
        }
    }

    /**
     * Print all messages stored in this run.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) return "No messages to display.";
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
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
     * OpenAI reading JSON file into an array 
     */
    public static void storeMessagesToFile() throws IOException {
        Path out = Path.of("messages.json");
        String json = GSON.toJson(sentMessages);
        // Atomically replace the file contents
        Files.writeString(out, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Return a details string used in GUI dialogs and logs.
     */
    public String getDetailsString() {
        return "MessageID: " + messageID + "\n" +
                "Message Hash: " + createMessageHash() + "\n" +
                "Recipient: " + recipientCell + "\n" +
                "Message: " + messageText;
    }

    @Override
    public String toString() {
        return "MessageID: " + messageID + ", Recipient: " + recipientCell + ", Message: " + messageText;
    }
}