package edu.berkeley.cs.cs162.Test;

import edu.berkeley.cs.cs162.Server.AuthenticationManager;
import edu.berkeley.cs.cs162.Server.AuthenticationManager.ServerAuthenticationException;
import edu.berkeley.cs.cs162.Server.DatabaseConnection;
import edu.berkeley.cs.cs162.Server.Security;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Test the AuthenticationManager.
 */

public class AuthenticationManagerTest {

    static AuthenticationManager am;
    static ClientInfo kunal;
    static String password;
    static DatabaseConnection db;

    @BeforeClass
    public static void setUp(){
        String dbname = "authmanager-test.db";

        try{
            db = new DatabaseConnection(dbname);
            db.initializeDatabase();
            am = new AuthenticationManager(db, "cs162project3istasty");
        }
        catch (SQLException e) { fail("SQL Exception in setup method."); }

        kunal = MessageFactory.createMachinePlayerClientInfo("kunal");
        password = Security.computeHash("kunal");
    }

    @AfterClass
    public static void tearDown(){
        db.wipeDatabase();
    }

    @Test /* Test that a client can successfully register, and cannot register twice. */
    public void testRegisterClient(){

        // First registration should be successful
        assertTrue("Kunal's first registration should be successful.", am.registerClient(kunal, password));

        // Second registration should be unsuccessful
        assertFalse("Kunal's second registration should be unsuccessful.", am.registerClient(kunal, password));
    }

    @Test /* Test that a registered client can successfully authenticate, but an unregistered client cannot. */
    public void testAuthenticateClient(){

        ClientInfo jay = MessageFactory.createMachinePlayerClientInfo("jay");
        String password2 = Security.computeHash("jay");

        // Kunal should be able to authenticate
        try { am.authenticateClient(kunal, password); }
        catch (ServerAuthenticationException e) { fail("Kunal should have been able to authenticate."); }

        // Jay should NOT be able to authenticate
        try { am.authenticateClient(jay, password2); fail("Jay should NOT have been able to authenticate.");}
        catch (ServerAuthenticationException e) { /* Nothing... */ }

    }

    @Test /* Test that the AuthenticationManager can correctly change passwords. */
    public void testChangePassword(){

        // Kunal should be able to authenticate
        try { am.authenticateClient(kunal, password); }
        catch (ServerAuthenticationException e) { fail("Kunal should have been able to authenticate."); }

        String newpassword = Security.computeHash("kunal2");
        am.changePassword(kunal, newpassword);

        // Kunal should be able to authenticate with his new password
        try { am.authenticateClient(kunal, newpassword); }
        catch (ServerAuthenticationException e) { fail("Kunal should have been able to authenticate with his new password."); }

        // Kunal should NOT be able to authenticate with his old password
        try { am.authenticateClient(kunal, password); fail("Kunal should NOT have been able to authenticate with his old password");}
        catch (ServerAuthenticationException e) { /* Nothing... */ }

    }
}
