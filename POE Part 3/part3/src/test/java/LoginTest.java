import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.Login;

public class LoginTest {

    @Test
    public void testCheckUserName() {
        // Use assertEquals for explicit true/false checks as requested
        assertEquals(true, Login.checkUserName("kyl_1"));
        assertEquals(false, Login.checkUserName("kyle!!!!!!"));

        assertEquals("Username is not formatted correctly - ensure that your username contains an underscore and is no longer than 5 characters in length", Login.formatUsernameError());
                String welcome = Login.formatWelcome("John", "Doe");
                assertEquals("Welcome John, Doe it is great to see you", welcome);
                
        assertTrue(Login.checkUserName("kyl_1"));
        assertFalse(Login.checkUserName("kyle!!!!!!"));

     
    }

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertEquals(true, Login.checkPasswordComplexity("Ch&&sec@ke99"));
        assertEquals(false, Login.checkPasswordComplexity("password"));
        assertEquals("password captured successfully", Login.formatPasswordSuccess());
        assertEquals("Incorrect password format ensure password is at least 8 characters in length, contains a capital letter, a number and a special character",Login.formatPasswordError());

        assertTrue(Login.checkPasswordComplexity("Ch&&sec@ke99"));
        assertFalse(Login.checkPasswordComplexity("password"));
        }

    @Test
    public void testCheckCellPhoneNumber_Valid() {
        // Basic boolean validations
        assertEquals(true, Login.checkCellPhoneNumber("+27838968976"));
        assertEquals(false, Login.checkCellPhoneNumber("08966554"));
        assertEquals("Phone number captured successfully", Login.formatPhoneSuccess());
        assertEquals("Phone number is not correctly formatted, please ensure that the phone number starts with +27 or 0 and is 10 digits long.", Login.formatPhoneError());

        // Keep original true/false assertions
        assertTrue(Login.checkCellPhoneNumber("+27838968976"));
        assertFalse(Login.checkCellPhoneNumber("08966554"));
    }

    @Test
    public void testLoginUser_Success() {
        Login user = new Login("kyl_1", "Password1!", "+2712345678");
        assertTrue(user.loginUser("kyl_1", "Password1!", "+2712345678"));
    }

    @Test
    public void testLoginUser_Failure() {
        Login user = new Login("kyl_1", "Password1!", "+2712345678");
        assertFalse(user.loginUser("kyl_1s", "wrongpass", "+2712345678"));
    }

    @Test
    public void testCheckUserName_Assertions() {
        assertTrue(Login.checkUserName("kyl_1"));
        assertFalse(Login.checkUserName("username"));
    }

    @Test
    public void testCheckPasswordComplexity_Assertions() {
        assertTrue(Login.checkPasswordComplexity("Ch&&sec@ke99"));
        assertFalse(Login.checkPasswordComplexity("password"));
    }
}
    