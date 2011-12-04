package edu.berkeley.cs.cs162.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import edu.berkeley.cs.cs162.Server.AuthenticationManager;
import edu.berkeley.cs.cs162.Server.DatabaseConnection;
import edu.berkeley.cs.cs162.Writable.ClientInfo;

public class TestAuthenticationManager extends AuthenticationManager{
	private ByteArrayOutputStream baos;
	private PrintStream ps;
	public TestAuthenticationManager(DatabaseConnection connection, String salt) {
		super(connection, salt);
		baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos);
	}
	/**
	 * Registers a client with the specified info and password hash. 
	 * 
	 * The hash should be salted before stored in the database.
	 * 
	 * The return value should be true if the client was successfully registered,
	 * or false if another client with the same name is already registered.
	 * 
	 * @param cInfo
	 * @param passwordHash
	 * @return success status of the registration.
	 */
	public boolean registerClient(ClientInfo cInfo, String passwordHash) {
		ps.println("Registered client with cInfo: " + cInfo.toString() + " and password hash: " + passwordHash);
		return true;
	}
	
	/**
	 * Authenticates a client.
	 * 
	 * Note that you should salt the password hash before comparing to the one stored in the database.
	 * 
	 * Returns true if password and player type matches the one logged in the database.
	 * 
	 * @param cInfo
	 * @param passwordHash
	 * @return success
	 */
	public int authenticateClient(ClientInfo cInfo, String passwordHash) {
		ps.println("Authenticated client with cInfo: " + cInfo.toString() + " and password hash: " + passwordHash);
		return clientIds++;
	}
	
	/**
	 * Changes a client's password 
	 * 
	 * This method assumes that the authentication and client info already has been checked.
	 * 
	 * This will change the password of a client to a salted version of newPasswordHash.
	 * 
	 * @param cInfo
	 * @param newPasswordHash
	 */
	public void changePassword(ClientInfo cInfo, String newPasswordHash) {
		ps.println("Changed password for client with cInfo: " + cInfo.toString() + " to password hash: " + newPasswordHash);
	}
}
