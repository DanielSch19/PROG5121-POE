import org.junit.Test;

import com.example.Login;

import static org.junit.Assert.*;

public class LoginTest {
    @Test
    public void testCheckUserName() {
        assertEquals(Login.checkUserName("kyl_1"), true);
        assertEquals(Login.checkUserName("kyle!!!!!!"), false);
    }

    

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertEquals(Login.checkPasswordComplexity("Ch&&sec@ke99"), true);
        assertEquals(Login.checkPasswordComplexity("password"), false);

    }


    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertEquals(Login.checkCellPhoneNumber("+27838968976"), true);
        assertEquals(Login.checkCellPhoneNumber("08966554"), false);
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
    