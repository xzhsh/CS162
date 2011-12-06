package edu.berkeley.cs.cs162.Server;

/**
 * Manager class for authenticating Clients.
 * 
 * This should be able to handle registering clients, authenticating clients, 
 * and changing client's passwords.
 * 
 * NOTES:
 * - All of these methods should be synchronized the Database connection! Do not lock the methods in the manager.
 * - If you call executeReadQuery(), remember to ALWAYS call closeReadQuery() on the result when you're done!
 */

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.berkeley.cs.cs162.Writable.ClientInfo;

public class AuthenticationManager {
	private DatabaseConnection connection;
	private String salt;
	
	public static class ServerAuthenticationException extends Exception{
		private static final long serialVersionUID = -1052874230279909816L;
	}

	//load this at the start and increment this to get client ids.
	protected int clientIds;
	
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
	 * @return success
	 */
	public boolean registerClient(ClientInfo cInfo, String passwordHash) {
        String clientName = cInfo.getName();
        byte clientType = cInfo.getPlayerType();
        String finalPass = Security.computeHashWithSalt(passwordHash, salt);
        
        ResultSet results = connection.executeReadQuery("SELECT name FROM clients WHERE name='" + clientName + "'");
        
        try {
        	boolean f = true;
            // If the ResultSet is not empty (i.e. the user already exists)
            while(results.next()){
            	System.out.println("Query " + cInfo.getName() + " found " + results.getString(1));
                f = false;
            }
            if (!f) {
            	return false;
            }
        }
        catch (SQLException e) { e.printStackTrace();/* Do nothing... */ }
        catch (NullPointerException e) { /* Do nothing... */ }
        finally {
        	connection.closeReadQuery(results);
        }
        
        connection.startTransaction();
        try {
            connection.executeWriteQuery("INSERT INTO clients (name, type, passwordHash) VALUES (" + "'" + clientName + "', " + Byte.toString(clientType) + ", '" + finalPass + "'" + ")");
            connection.finishTransaction();
            return true;
        }
        catch(SQLException e){
            connection.abortTransaction();
            return false;
        }
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
	 * @return client id of the new client.
     * @throws ServerAuthenticationException in the case of authentication failure
	 */
	public int authenticateClient(ClientInfo cInfo, String passwordHash) throws ServerAuthenticationException {
        String clientName = cInfo.getName();
        String finalPass = Security.computeHashWithSalt(passwordHash, salt);
        ResultSet results = null;
        
        try {
            results = connection.executeReadQuery("SELECT passwordHash, clientId FROM clients WHERE name='" + clientName + "'");

            if(results == null){
                throw new ServerAuthenticationException();
            }
            else if (!results.next()) {
                throw new ServerAuthenticationException();
            }
            else if (results.getString("passwordHash").equals(finalPass)) {
                int cid = results.getInt("clientId");
                assert cid != -1;
                return cid;
            }
            else{
                throw new ServerAuthenticationException();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new ServerAuthenticationException();
        } finally {
        	if (results != null)
        		connection.closeReadQuery(results);
        }
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
        String clientName = cInfo.getName();
        ResultSet result = null;
        String query = null;
        try {
            String cidQuery = "SELECT clientId FROM clients WHERE name='" + clientName + "'";
            result = connection.executeReadQuery(cidQuery);

            try { if(!result.next()) return; }
            catch (SQLException e) { /* Do nothing... */ }
            catch (NullPointerException e) { /* Do nothing... */ }

            int clientID = result.getInt("clientId");
            query = "UPDATE clients SET passwordHash='" + Security.computeHashWithSalt(newPasswordHash, salt) + "' WHERE clientId=" + Integer.toString(clientID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connection.closeReadQuery(result);
        } try {
	        connection.startTransaction();
	        connection.executeWriteQuery(query);
        } catch (SQLException e) {
        	throw new RuntimeException(e);
        } finally {
        	connection.finishTransaction();
        }
	}
}