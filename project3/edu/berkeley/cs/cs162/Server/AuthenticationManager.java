package edu.berkeley.cs.cs162.Server;

/**
 * Manager class for authenticating Clients.
 * 
 * This should be able to handle registering clients, authenticating clients, 
 * and changing client's passwords.
 * 
 * NOTE: All of these methods should be synchronized the Database connection!
 * 		 do not lock the methods in the manager.
 */
import edu.berkeley.cs.cs162.Writable.ClientInfo;

public class AuthenticationManager {
	@SuppressWarnings("unused")
	private DatabaseConnection connection;
	@SuppressWarnings("unused")
	private String salt;

	/**
	 * Constructs an authentication manager with a connection to the database and a salt.
	 * @param connection Connection to the database.
	 * @param salt Salt string for password hashing.
	 */
	public AuthenticationManager(DatabaseConnection connection, String salt) {
		this.connection = connection;
		this.salt = salt;
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
		throw new RuntimeException("Unimplemented Method");
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
	public boolean authenticateClient(ClientInfo cInfo, String passwordHash) {
		throw new RuntimeException("Unimplemented Method");
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
		throw new RuntimeException("Unimplemented Method");
	}
}