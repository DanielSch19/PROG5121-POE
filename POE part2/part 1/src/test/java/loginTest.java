import org.junit.Test;

import com.example.login;

import static org.junit.Assert.*;

public class loginTest {
    @Test
    public void testCheckUserName() {
        assertEquals(login.checkUserName("kyl_1"), true);
        assertEquals(login.checkUserName("kyle!!!!!!"), false);
    }

    

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertEquals(login.checkPasswordComplexity("Ch&&sec@ke99"), true);
        assertEquals(login.checkPasswordComplexity("password"), false);
        
    }


    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertEquals(login.checkCellPhoneNumber("+27838968976"), true);
        assertEquals(login.checkCellPhoneNumber("08966554"), false);
    }


    @Test
    public void testLoginUser_Success() {
        login user = new login("kyl_1", "Password1!", "+2712345678");
        assertTrue(user.loginUser("kyl_1", "Password1!", "+2712345678"));
    }

    @Test
    public void testLoginUser_Failure() {
        login user = new login("kyl_1", "Password1!", "+2712345678");
        assertFalse(user.loginUser("kyl_1s", "wrongpass", "+2712345678"));
    }


    @Test
    public void testCheckUserName_Assertions() {
        assertTrue(login.checkUserName("kyl_1"));
        assertFalse(login.checkUserName("username"));
    }

    @Test
    public void testCheckPasswordComplexity_Assertions() {
    assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99"));
    assertFalse(login.checkPasswordComplexity("password"));
    }
}