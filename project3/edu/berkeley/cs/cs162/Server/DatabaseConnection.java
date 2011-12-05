package edu.berkeley.cs.cs162.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
/**
 * DatabaseConnection abstracts a connection to a remote database.
 * 
 * Supported operations include writing game state, loading gameserver state
 * writing and loading client info.
 * 
 * This abstraction should also provide synchronization. The callers of these 
 * methods will assume correctness over multiple threads.
 * 
 * @author xshi
 *
 */
public class DatabaseConnection {
	private Connection canonicalConnection;
	ReaderWriterLock dataLock;
	
	public DatabaseConnection(String databasePath) throws SQLException
	{
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find sqlite JDBC class. Did you include the correct jar in the build path?");
		}
	    canonicalConnection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        dataLock = new ReaderWriterLock();
	}

    /**
     * Initializes the database, creating the necessary tables
     */
    private void initializeDatabase(){

    }
	
	/**
	 * Starts a transaction. It will not be committed or be interrupted until finish transaction is called.
	 * 
	 * @return the connection to start the transaction.
	 */
	public Connection startTransaction() {
		dataLock.writeLock();
		try {
			canonicalConnection.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canonicalConnection;
	}
	
	/**
	 * Unlocks the write lock, and commits the transaction.
	 */
	public void finishTransaction() {
		try {
			canonicalConnection.commit();
			canonicalConnection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			dataLock.writeUnlock();
		}
	}

	/**
	 * executes a single read
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeReadQuery(String query) throws SQLException {
		dataLock.readLock();
		Statement readQuery = null;
		ResultSet rs = null;
		try {
			readQuery = canonicalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		      
            if (!readQuery.execute(query)) {
                System.err.println("Could not find entry");
            }
            else {
                rs = readQuery.getResultSet();
            }
		} catch (SQLException e) {
		      e.printStackTrace();
		} finally {
            if (readQuery != null) readQuery.close();
			dataLock.readUnlock();
		}
		return rs;
	}
	
	/**
	 * Executes a single write
	 * @param query
	 * @throws SQLException
     * @return true if the write operation was successful, false otherwise.
	 */
	public boolean executeWriteQuery(String query) throws SQLException{

		Statement writeQuery = null;
        boolean success = false;

		try {
			writeQuery = canonicalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		    writeQuery.execute(query);
            success = true;
		}
        catch (SQLException e) {
		      e.printStackTrace();
		}
        finally {
			if (writeQuery != null) {writeQuery.close();}
		}

        return success;
	}
}