# Prog5121 POE-ST10480308
Daniel Schmdit
Java chatapp

# QuickChat – User Registration, Login, and Messaging System

---

##  Overview

**QuickChat** is a Java Swing-based desktop application that allows users to:
- Register an account with validation for username, password, and phone number.
- Log in using their registered credentials.
- Send, store, or disregard chat messages.
- Save messages to a JSON file and review sent messages.

It includes robust input validation, object-oriented design, and comprehensive **JUnit 5** test coverage for both **Login** and **Message** functionalities.

---

##  Features



###  User Registration
Captures and validates:
- Username  
- Password  
- Phone number  
- Name  
- Surname  

Ensures all inputs follow strict formatting and security requirements.

---

###  Input Validation Rules

| Field           | Validation Rule                                         | Example (✓)      | Invalid (✗)   |
|-----------------|--------------------------------------------------------|-------------------|---------------|
| **Username**    | Must contain `_` and be ≤ 5 characters                 | `kyl_1`           | `username`    |
| **Password**    | ≥ 8 chars, includes uppercase, lowercase, number, and special char | `Ch&&sec@ke99!`   | `password`    |
| **Phone Number**| Must start with `+27` and be exactly 12 characters     | `+27821234567`    | `27821234567` |

---

##  Messaging System

###  Message Features
Each message includes:
- **Message ID** (10-digit random number)
- **Recipient number** (+27 format)
- **Content**
- **Message hash** (unique identifier)

Users can choose to:
- **Send Now**
- **Store for Later** (saved to `messages.json`)
- **Disregard**

###  Arrays
- The application uses in-memory collections (lists/arrays) to manage messages and related metadata.
- Key collections in `Message.java` include: `sentMessages`, `storedMessages`, `disregardedMessages`, `messageHashes`, and `messageIDs`.

---

###  Message Validation
- Recipient number must follow `+27XXXXXXXXX` format.  
- Messages must be **≤ 250 characters**.  
- Messages can be printed using `Message.printMessages()`.

---

##  Testing

JUnit 5 tests cover all **Login** and **Message** functionalities.

### `LoginTest.java`
- Username validation (valid/invalid)  
- Password complexity validation  
- Phone number format validation  
- Login authentication (success/failure)

### `MessageTest.java`
- Message length validation  
- Recipient phone number validation  
- Message ID and hash format checks  
- File creation and JSON storage tests  
- Array expansion (dynamic storage)  
- Total messages counter  
- Print all messages functionality  
- Validate dynamic behavior such as adding, deleting, and restoring messages, and ensure counters (e.g. total messages sent) update correctly.
- Persisted messages are written to `messages.json` and restored into these collections on load.
---

##  Project Structure

```
Prog5121-ST10466183/
├── .idea/
├── .vscode/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Login.java         # Handles registration and login logic
│   │   │   ├── Main.java          # Entry point of the application
│   │   │   ├── MainGUI.java       # Swing-based GUI (Registration, Login, Chat)
│   │   │   └── Message.java       # Handles message creation, storage, validation
│   │   └── resources/
│   └── test/java/
│       ├── LoginTest.java         # JUnit tests for Login class
│       └── MessageTest.java       # JUnit tests for Message class
├── target/
├── pom.xml                        # Maven configuration
└── Github link.txt                # Repository URL
```

---

##  Example Run

```
-- Registration --
Enter a Username (must have '_' and ≤5 characters): kyl_1
Username successfully captured.
Enter Name: John
Enter Surname: Doe
Enter Password (min 8 chars with uppercase, number, special): Password1!
Password successfully captured.
Enter Phone Number (must include +27): +27821234567
Phone number successfully added.

Registration complete. Now please log in.

-- Login --
Enter username: kyl_1
Enter password: Password1!
Welcome John Doe, it is great to see you again.

-- Quick Chat --
Recipient (+27 format): +27888968976
Message: Hi there, let’s meet up later!
Choose Action: [Send Now | Store Message | Disregard]
Message sent.
Quit
```

---

##  Dependencies

| Dependency                              | Version   | Purpose                |
|------------------------------------------|-----------|------------------------|
| `org.junit.jupiter:junit-jupiter`        | 5.10.2    | Unit testing           |
| `com.googlecode.json-simple:json-simple` | 1.1.1     | JSON writing support   |
| `org.json:json`                         | 20250517  | JSON object manipulation|

---

##  Build Configuration

**Java Version:** 21  
**Maven Compiler Plugin:** 3.11.0  

###  Build Command

```bash
mvn clean compile
```

## Part 3 (module)

The `part3` module contains the latest iteration of the messaging features for this POE project. Key files and behavior:

- `part3/src/main/java/com/example/Message.java` — core message model and utilities (create, validate, send, store, load).
- `part3/src/test/java/` — unit tests for `Message` and related classes; tests produce Surefire reports in `part3/target/surefire-reports/` when run with Maven.
- `part3/messages.json` — sample / runtime JSON file used to persist stored messages between runs. The code reads `part3/messages.json` when present, otherwise falls back to a top-level `messages.json`.

Running tests for the `part3` module:

```
cd part3
mvn test
```

When running in CI (GitHub Actions) the recommended workflow runs `mvn -B -DskipTests=false test` and collects Surefire reports for test reporting.

## References

- Using AI tools can assist with programming tasks (ChatGPT, personal communication, 3 September 2025; GitHub, 2025).
- ChatGPT. (2025) Response to programming query on user registration and login system. Personal communication with Thorn Scheepers, 3 September.
- OpenAI. (2025) ChatGPT [AI language model]. Available at: https://chat.openai.com/ (Accessed: 3 September 2025).
- GitHub. (2025) GitHub Copilot [AI code generation tool]. Available at: https://github.com/features/copilot (Accessed: 3 September 2025).
- ChatGPT. (2025) Assistance with JSON file and storage. (Accessed: 9 October 2025).
- ChatGPT. (2025) Response to programming query on JUnit 5 GitHub Actions workflow. Personal communication with Thorn Scheepers, 12 October.
- GitHub Copilot. (2025) AI code generation tool. Available at: https://github.com/features/copilot (Accessed: 12 October 2025).
- OpenAI. (2025) ChatGPT [AI language model]. Response to query on Java Maven project structure. Personal communication with Thorn Scheepers (Accessed: 5 October 2025).
- ChatGPT. (2025) Assistance with Java JUnit array handling and file storage. (Accessed: 18 November 2025).
- GitHub. (2025) GitHub Copilot [AI code generation tool].Assistance with automating unit tests. Available at: https://github.com/features/copilot (Accessed: 18 November 2025).


## Licence

This project is for educational purposes.
