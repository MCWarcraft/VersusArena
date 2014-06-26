package austin.CavemanSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/** Queries the database
 * 
 * @author Austin
 *
 */
public class DatabaseQueryAction
{
	private Connection connection;
	private String tableName;
	private String fields;
	private HashMap<String, Integer> intConstraints;
	private HashMap<String, String> stringConstraints;
	private HashMap<String, Boolean> booleanConstraints;
	private HashMap<String, Double> doubleConstraints;
	private boolean areConstraints;
	
	protected DatabaseQueryAction(Connection connection, String tableName)
	{
		this.connection = connection;
		this.tableName = tableName;
		
		fields = "*";
		
		areConstraints = false;
		
		stringConstraints = new HashMap<String, String>();
		intConstraints = new HashMap<String, Integer>();
		doubleConstraints = new HashMap<String, Double>();
		booleanConstraints = new HashMap<String, Boolean>();
		
	}
	/** Sets what data to pull in the query. If not used, defaults to "*" - all
	 * 
	 * @param fields A list of the columns to pull, separated by a comma. "name, score, hunger"
	 */
	public void setFields(String fields)
	{
		this.fields = fields;
	}
	
	/** Adds a constraint to the data being queried
	 * 
	 * @param column The column for which there will be a constraint
	 * @param value The value of that column that must be fulfilled
	 */
	public void addConstraint(String column, String value)
	{
		areConstraints = true;
		stringConstraints.put(column, value);
	}
	
	/** Adds a constraint to the data being queried
	 * 
	 * @param column The column for which there will be a constraint
	 * @param value The value of that column that must be fulfilled
	 */
	public void addConstraint(String column, int value)
	{
		areConstraints = true;
		intConstraints.put(column, value);
	}
	
	/** Adds a constraint to the data being queried
	 * 
	 * @param column The column for which there will be a constraint
	 * @param value The value of that column that must be fulfilled
	 */
	public void addConstraint(String column, double value)
	{
		areConstraints = true;
		doubleConstraints.put(column, value);
	}
	
	/** Adds a constraint to the data being queried
	 * 
	 * @param column The column for which there will be a constraint
	 * @param value The value of that column that must be fulfilled
	 */
	public void addConstraint(String column, boolean value)
	{
		areConstraints = true;
		booleanConstraints.put(column, value);
	}
	
	/** Executes the query using the fields and constraints that have been set
	 * 
	 * @return A ResultSet with the result of the query.
	 */
	public ResultSet executeQuery()
	{
		try
		{		
			//Built statement
			String statementString = "SELECT " + fields + " from " + tableName;
			
			PreparedStatement saveStatement = connection.prepareStatement(statementString);
			
			if (areConstraints)
			{
				statementString += " WHERE ";
				
				for (String key : stringConstraints.keySet())
					statementString = statementString + key + "=? AND ";
				for (String key : intConstraints.keySet())
					statementString = statementString + key + "=? AND ";
				for (String key : doubleConstraints.keySet())
					statementString = statementString + key + "=? AND ";
				for (String key : booleanConstraints.keySet())
					statementString = statementString + key + "=? AND ";
				
				statementString = statementString.substring(0, statementString.length() - 5);
							
				saveStatement = connection.prepareStatement(statementString);
	
				int number = 1;
				for (String key : stringConstraints.keySet())
				{
					saveStatement.setString(number, stringConstraints.get(key));
					number++;
				}
				for (String key : intConstraints.keySet())
				{
					saveStatement.setString(number, stringConstraints.get(key));
					number++;
				}
				for (String key : doubleConstraints.keySet())
				{
					saveStatement.setString(number, stringConstraints.get(key));
					number++;
				}
				for (String key : booleanConstraints.keySet())
				{
					saveStatement.setString(number, stringConstraints.get(key));
					number++;
				}
			}
			
			return saveStatement.executeQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
