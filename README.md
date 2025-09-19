# PROG5121-POE
Daniel Schmidt : ST10480308
# User Registration and Login System

## Features

- **User Registration**: Captures username, password, and phone number with validation
- **Input Validation**: Enforces specific formatting rules for all user inputs
- **User Authentication**: Allows users to log in with their registered credentials
- **Welcome Message**: Displays a personalized greeting after successful login

## Validation Rules

### Username
- Must contain an underscore (_)
- Must be no more than 5 characters in length
- Example: `kyl_1` ✓, `username` ✗

### Password
- Must be at least 8 characters long
- Must contain at least one uppercase letter
- Must contain at least one lowercase letter
- Must contain at least one number
- Must contain at least one special character
- Example: `Ch&&sec@ke99` ✓, `password` ✗

### Phone Number
- Must start with either `+27` or `0`
- Must be exactly 10 digits in total length
- Example: `+27123456789` ✓, `0123456789` ✓, `123456` ✗

## Project Structure

```
poe/
├── src/
│   ├── main/java/com/example/
│   │   ├── Main.java          # Main application entry point
│   │   └── login.java         # User authentication and validation logic
│   └── test/java/
│       └── loginTest.java     # Unit tests for login functionality
├── pom.xml                    # Maven dependencies
└── README.md                  # This file
```

## Getting Started

## Usage

1. **Registration Phase**:
   - Enter a username (must contain underscore, max 5 chars)
   - Enter a password (min 8 chars, uppercase, lowercase, number, special char)
   - Enter a phone number (starts with +27 or 0, 10 digits total)

2. **Login Phase**:
   - Enter your registered username
   - Enter your registered password
   - Enter your registered phone number

3. **Welcome Message**:
   - After successful login, enter your first and last name
   - Receive a personalized welcome message

## Example Run

```
******Registration******
Enter a user name: kyl_1
Username successfully captured
Enter a password: Ch&&sec@ke99
Password successfully captured
Enter a phone number: +27123456789
Phone number successfully captured
Registration successful!

******Login******
Enter your Username: kyl_1
Enter your password: Ch&&sec@ke99
Enter your phone number: +27123456789
Login successful!
Enter your first name: John
Enter your last name: Doe
Welcome John Doe it's great to see you again.
```

## Testing

The project includes comprehensive unit tests covering:
- Username validation (valid and invalid cases)
- Password complexity validation
- Phone number format validation
- User login functionality (success and failure scenarios)

Test coverage includes edge cases and boundary conditions for all validation rules.

## Dependencies

- **JUnit 4.13.2**: For unit testing

## License

This project is for educational purposes.
