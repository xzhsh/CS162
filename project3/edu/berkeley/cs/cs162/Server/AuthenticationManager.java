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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.berkeley.cs.cs162.Writable.ClientInfo;

public class AuthenticationManager {
	@SuppressWarnings("unused")
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

        String rehashedPassword = Security.computeHashWithSalt(passwordHash, salt);

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
	 * @return client id of the new client.
	 */
	public int authenticateClient(ClientInfo cInfo, String passwordHash) throws ServerAuthenticationException{

        String rehashedPassword = Security.computeHashWithSalt(passwordHash, salt);

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
        DatabaseConnection connection = null;
        Connection con = null;

        try {
            connection = new DatabaseConnection("");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String clientName = cInfo.getName();

        String query = "";

        con = connection.startTransaction();

        try {
            String cidQuery = "select clientID from dbname.clients where name=" + clientName;

            ResultSet result = con.prepareStatement(cidQuery).getResultSet();

            int clientID = result.getInt(1);

            query = "update dbname.clients set passwordHash=" + Security.computeHashWithSalt(newPasswordHash, salt) + " where clientID=" + Integer.toString(clientID);

            (con.prepareStatement(query)).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection.finishTransaction();
		
//		String pName = cInfo.getName();
//		int CID;
//		PreparedStatement getCID = null;
//		PreparedStatement setNewPW = null;
//		ResultSet prs = null;
//		Connection con = connection.startTransaction();
//		try {
//			String getCIDQuery= "select clientID from dbname.clients where name= " + pName;
//			getCID = con.prepareStatement(getCIDQuery);
//			prs = getCID.executeQuery();
//			CID = prs.getInt(1);
//			String newPWQuery = "update dbname.clients set passwordHash= " + newPasswordHash+salt + "where clientID= " + Integer.toString(CID);
//			setNewPW = con.prepareStatement(newPWQuery);
//			setNewPW.execute();
//		} catch (SQLExeption e) {
//			e.printStackTrace();
//		}
//		connection.finishTransaction();
		throw new RuntimeException("Unimplemented Method");
	}
}