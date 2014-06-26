package austin.CavemanSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/** Updates the database
 * 
 * @author Austin
 *
 */
public class DatabaseUpdateAction
{
	private HashMap<String, String> stringValues;
	private HashMap<String, Integer> intValues;
	private HashMap<String, Double> doubleValues;
	private HashMap<String, Boolean> booleanValues;
	
	private Connection connection;
	private String tableName;
	
	private PrimaryType primaryType;
	private int intPrimary;
	private String stringPrimary;
	
	protected DatabaseUpdateAction(Connection connection, String tableName)
	{
		stringValues = new HashMap<String, String>();
		intValues = new HashMap<String, Integer>();
		doubleValues = new HashMap<String, Double>();
		booleanValues = new HashMap<String, Boolean>();
		
		this.connection = connection;
		this.tableName = tableName;
		
		primaryType = PrimaryType.NOT_SET;
		intPrimary = -1;
		stringPrimary = "";
	}
	
	/** Sets a string value to be updated
	 * 
	 * @param column The column in which the value will be set
	 * @param value The string that will be put in that column
	 */
	public void setString(String column, String value)
	{
		stringValues.put(column, value);
	}
	
	/** Sets an int value to be updated
	 * 
	 * @param column The column in which the value will be set
	 * @param value The int that will be put in that column
	 */
	public void setInt(String column, int value)
	{
		intValues.put(column, value);
	}
	
	/** Sets a double value to be updated
	 * 
	 * @param column The column in which the value will be set
	 * @param value The double that will be put in that column
	 */
	public void setDouble(String column, double value)
	{
		doubleValues.put(column, value);
	}
	
	/** Sets a boolean value to be updated
	 * 
	 * @param column The column in which the value will be set
	 * @param value The boolean that will be put in that column
	 */
	public void setBoolean(String column, boolean value)
	{
		booleanValues.put(column, value);
	}
	
	/** Sets the primary key for which data is being set
	 * 
	 * @param key The value of the primary key for which the data is being set
	 */
	public void setPrimaryValue(String key)
	{
		primaryType = PrimaryType.STRING;
		stringPrimary = key;
	}
	
	/** Sets the primary key for which data is being set
	 * 
	 * @param key The value of the primary key for which the data is being set
	 */
	public void setPrimaryValue(int key)
	{
		primaryType = PrimaryType.INT;
		intPrimary = key;
	}
	
	/** Executes the update that has been set up.
	 * 
	 * @return true unless primary key is not set
	 */
	public boolean executeUpdate()
	{
		
		if (primaryType == PrimaryType.NOT_SET) return false;
		PreparedStatement saveStatement = null;
		try
		{
			ResultSet primaryKeyResultSet = connection.prepareStatement("SHOW KEYS FROM " + tableName + " WHERE Key_name = 'PRIMARY'").executeQuery();
			if (!primaryKeyResultSet.next()) return false;
			
			String primaryKeyName = primaryKeyResultSet.getString("Column_name");
			
			boolean dataExists;
			
			if (primaryType == PrimaryType.INT)
				dataExists = dataExists(tableName, primaryKeyName, intPrimary);
			else if (primaryType == PrimaryType.STRING)
				dataExists = dataExists(tableName, primaryKeyName, stringPrimary);
			else
				return false;
			
			//Built statement
			String statementString = (dataExists ? "UPDATE" : "INSERT INTO") + " " + tableName + " SET ";
			for (String key : stringValues.keySet())
				statementString = statementString + key + "=?,";
			for (String key : intValues.keySet())
				statementString = statementString + key + "=?,";
			for (String key : doubleValues.keySet())
				statementString = statementString + key + "=?,";
			for (String key : booleanValues.keySet())
				statementString = statementString + key + "=?,";
			
			statementString = statementString.substring(0, statementString.length() - 1) + " " + (dataExists ? "WHERE " + primaryKeyName +"=?" : "," + primaryKeyName + "=?");
			
			saveStatement = connection.prepareStatement(statementString);

			int number = 1;
			for (String key : stringValues.keySet())
			{
				saveStatement.setString(number, stringValues.get(key));
				number++;
			}
			for (String key : intValues.keySet())
			{
				saveStatement.setInt(number, intValues.get(key));
				number++;
			}
			for (String key : doubleValues.keySet())
			{
				saveStatement.setDouble(number, doubleValues.get(key));
				number++;
			}
			for (String key : booleanValues.keySet())
			{
				saveStatement.setBoolean(number, booleanValues.get(key));
				number++;
			}
			
			if (primaryType == PrimaryType.INT)
				saveStatement.setInt(number, intPrimary);
			else if (primaryType == PrimaryType.STRING)
				saveStatement.setString(number, stringPrimary);
			
			if (dataExists)
				saveStatement.executeUpdate();
			else
				saveStatement.execute();
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private synchronized boolean dataExists(String table, String colName, String value)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + colName + " = '" + value +"'");
			ResultSet result = statement.executeQuery();
			boolean isInRow = result.next();
			
			statement.close();
			result.close();
			
			return isInRow;
		}
		catch (SQLException e)
		{
			return false;
		}
	}
	
	private synchronized boolean dataExists(String table, String colName, int value)
	{
		try
		{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + colName + " = " + value);
			ResultSet result = statement.executeQuery();
			boolean isInRow = result.next();
			
			statement.close();
			result.close();
			
			return isInRow;
		}
		catch (SQLException e)
		{
			return false;
		}
	}
}
