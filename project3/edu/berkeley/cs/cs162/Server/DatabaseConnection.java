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
        canonicalConnection.setAutoCommit(false);
        dataLock = new ReaderWriterLock();
        initializeDatabase();
	}

    /**
     * Initializes the database, creating the necessary tables
     */
    public void initializeDatabase(){
    	dataLock.writeLock();
        try{
        	Statement writeQuery = canonicalConnection.createStatement();
            writeQuery.execute("create table if not exists clients (clientId integer primary key autoincrement, name text unique not null, type int not null, passwordHash text not null)");
            writeQuery.execute("create table if not exists games (gameId integer primary key autoincrement, blackPlayer int references clients (clientId) not null, whitePlayer int references clients (clientId) not null, boardSize int not null, blackScore real, whiteScore real, winner int references clients (clientId), moveNum int not null, reason int)");
            writeQuery.execute("create table if not exists moves (moveId integer primary key autoincrement, clientId int references clients (clientId) not null, gameId int references games (gameId) not null, moveType int not null, x int, y int, moveNum int not null)");
            writeQuery.execute("create table if not exists captured_stones (stoneId integer primary key autoincrement, moveId int references moves (moveId), x int, y int)");
            writeQuery.close();
            canonicalConnection.commit();
        }
        catch(SQLException e){
            e.printStackTrace();
            abortTransaction();
        }
        finally {
        	dataLock.writeUnlock();
        }
    }
	
	/**
	 * Starts a transaction. It will not be committed or be interrupted until finish transaction is called.
	 */
	public void startTransaction() {
		dataLock.writeLock();
	}
	
	/**
	 * Unlocks the write lock, and commits the transaction.
	 */
	public void finishTransaction() {
		try { canonicalConnection.commit(); }
        catch (SQLException e) { e.printStackTrace(); }
		finally { dataLock.writeUnlock(); }
	}

    /**
     * Aborts the current transaction. Used in the case of an SQLException while writing.
     */
    public void abortTransaction() {
        try { canonicalConnection.rollback(); }
        catch (SQLException e) { e.printStackTrace(); }
        finally { dataLock.writeUnlock(); }
    }

	/**
	 * Executes a single read.
     * Remember to call closeReadQuery() on the result when you're done!
     *
	 * @param query - The query to execute
	 * @return - A ResultSet corresponding to the query
	 */
	public ResultSet executeReadQuery(String query) {
		dataLock.readLock();
		Statement readQuery = null;
		ResultSet rs = null;

        try {
			readQuery = canonicalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = readQuery.executeQuery(query);
		}
        catch (SQLException e) {
		    e.printStackTrace();
		}
		return rs;
	}

    /**
     * Used to close a ResultSet and its corresponding Statement after using its data.
     * It also unlocks the readLock, to prevent another thread from writing to the database
     * while the Statement is open.
     *
     * USE THIS -EVERY- TIME YOU EXECUTE A READ QUERY!
     * (ESPECIALLY BEFORE EXECUTING A WRITE QUERY!)
     *
     * @param rs - The currently open ResultSet
     */
    public void closeReadQuery(ResultSet rs){
        try { rs.getStatement().close(); }
        catch (SQLException e) { /* Do nothing... */ }
        dataLock.readUnlock();
    }


	/**
	 * Executes a single write.
     *
	 * @param query - The query to execute.
	 * @throws SQLException - In the case of a database connecton error. This will
     * be caught upstream and used to abort the transaction.
     * @return The key generated as a result of the write operation, -1 if none.
	 */
	public int executeWriteQuery(String query) throws SQLException{

		Statement writeQuery = null;
        int generatedKey = -1;
        ResultSet keys = null;
		try {
			writeQuery = canonicalConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		    writeQuery.execute(query);
            keys = writeQuery.getGeneratedKeys();
            if(keys.next())
                generatedKey = keys.getInt(1);
            writeQuery.close();
		}
        catch (SQLException e) {
		    e.printStackTrace();
            if (writeQuery != null) writeQuery.close();
            throw e; // This needs to be caught upstream so that the transaction can be aborted.
        } finally {
        	if (keys != null) {
        		keys.close();	
        	}
        }
        return generatedKey;
	}

    /**
     * TESTING PURPOSES ONLY. This wipes the database clean; used by the AuthenticationManagerTest.
     */
    public void wipeDatabase(){
    	dataLock.writeLock();
        try{
        	Statement writeQuery = canonicalConnection.createStatement();
        	writeQuery.addBatch("drop table if exists clients");
        	writeQuery.addBatch("drop table if exists captured_stones");
        	writeQuery.addBatch("drop table if exists games");
        	writeQuery.addBatch("drop table if exists moves");
        	writeQuery.executeBatch();
        	writeQuery.close();
            canonicalConnection.commit();
        }
        catch(SQLException e){
        	e.printStackTrace();
            //abortTransaction();
            throw new RuntimeException(e);
        }finally {
        	dataLock.writeUnlock();
        }
    }

	public void close() {
		try {
			canonicalConnection.close();
		} catch (SQLException e) {
		}
	}
}