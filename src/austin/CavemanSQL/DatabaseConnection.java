package austin.CavemanSQL;

import java.sql.Connection;
import java.sql.DriverManager;

/** The central object used to generate DatabaseActions
 * 
 * @author Austin
 *
 */
public class DatabaseConnection
{
	private Connection connection;
	
	/**
	 * 
	 * @param ip The ip of the SQL server to connect to
	 * @param port The port of the SQL server to connect to
	 * @param database The name of the database on the SQL server
	 * @param username SQL username
	 * @param password SQL password
	 */
	public DatabaseConnection(String ip, String port, String database, String username, String password)
	{
		String connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + database;
		
		try
		{
			connection = DriverManager.getConnection(connectionString, username, password);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param tableName The name of the table to be updated
	 * @return A new DatabaseUpdateAction pointed at table tableName
	 */
	public DatabaseUpdateAction getDatabaseUpdateAction(String tableName)
	{
		return new DatabaseUpdateAction(connection, tableName);
	}
	
	/**
	 * 
	 * @param tableName The name of the table to be queried
	 * @return A new DatabaseQueryAction pointed at table tableName
	 */
	public DatabaseQueryAction getDatabaseQueryAction(String tableName)
	{
		return new DatabaseQueryAction(connection, tableName);
	}
}
