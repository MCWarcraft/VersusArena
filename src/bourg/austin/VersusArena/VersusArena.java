package bourg.austin.VersusArena;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Background.MyCommandExecutor;
import bourg.austin.VersusArena.Background.MyListener;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.VersusKit;

public final class VersusArena extends JavaPlugin
{
	private ArenaManager arenaManager;
	
	private HashMap<String, Location> selectedLocations;
	
	private Connection connection;
	
	private int soupHealAmount;
	
	public void onEnable()
	{		
		this.saveDefaultConfig();
		checkDatabase();
		
		Inventories.initialize();
		
		//Declare variables
		arenaManager = new ArenaManager(this);
		selectedLocations = new HashMap<String, Location>();

		//Set event listeners
		this.getServer().getPluginManager().registerEvents(new MyListener(this), this);
		
		//Set command executors
		this.getCommand("versus").setExecutor(new MyCommandExecutor(this));
		
		this.loadData();
	} 		  
	
	public void onDisable()
	{
		saveDatabase();
	}
	
	public synchronized boolean isStringInCol(String table, String colName, String rowName, boolean maintainConnection)
	{
		if (!maintainConnection)
			openConnection();
		try
		{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + colName + " = '" + rowName +"'");
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
		finally
		{
			if (!maintainConnection)
				closeConnection();
		}
	}
	
	public synchronized boolean isColInTable(String table, String colName, boolean maintainConnection)
	{
		if (!maintainConnection)
			openConnection();
		
		try
		{
			PreparedStatement getIfColExistsStatement = connection.prepareStatement("SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?");
			getIfColExistsStatement.setString(1, table);
			getIfColExistsStatement.setString(2, colName);
			return getIfColExistsStatement.executeQuery().next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (!maintainConnection)
				closeConnection();
		}
	}
	
	private synchronized void checkDatabase()
	{
		if (!openConnection())
			return;
		
		try
		{
			//Configure main player data table
			PreparedStatement openPlayerDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_data" +
					"( player varchar(17) not null," +
						"wins1 int DEFAULT 0," +
						"losses1 int DEFAULT 0," +
						"rating1 int DEFAULT 1500," +
						"wins2 int DEFAULT 0," +
						"losses2 int DEFAULT 0," +
						"rating2 int DEFAULT 1500," +
						"wins3 int DEFAULT 0," +
						"losses3 int DEFAULT 0," +
						"rating3 int DEFAULT 1500," +
						"PRIMARY KEY (player) " +
					")");
			openPlayerDataStatement.execute();
			openPlayerDataStatement.close();
			
			//Configure tables for arenas
			PreparedStatement openSinglesArenaDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS singles_arena_data" +
					"( name varchar(17) not null," +
						"team1player1 varchar(255)," +
						"team2player1 varchar(255)," +
						"deathlocation varchar(255)," +
						"PRIMARY KEY (name) " +
					")");
			openSinglesArenaDataStatement.execute();
			openSinglesArenaDataStatement.close();
			
			//Configure databases for doubles arenas
			PreparedStatement openDoublesArenaDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS doubles_arena_data" +
					"( name varchar(17) not null," +
						"team1player1 varchar(255)," +
						"team1player2 varchar(255)," +
						"team2player1 varchar(255)," +
						"team2player2 varchar(255)," +
						"deathlocation varchar(255)," +
						"PRIMARY KEY (name) " +
					")");
			openDoublesArenaDataStatement.execute();
			openDoublesArenaDataStatement.close();
			
			//Configure databases for triples arenas
			PreparedStatement openTriplesArenaDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS triples_arena_data" +
					"( name varchar(17) not null," +
						"team1player1 varchar(255)," +
						"team1player2 varchar(255)," +
						"team1player3 varchar(255)," +
						"team2player1 varchar(255)," +
						"team2player2 varchar(255)," +
						"team2player3 varchar(255)," +
						"deathlocation varchar(255)," +
						"PRIMARY KEY (name) " +
					")");
			openTriplesArenaDataStatement.execute();
			openTriplesArenaDataStatement.close();
			
			//Configure databases for nexus
			PreparedStatement openNexusDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS nexus_data (name varchar(17) not null, location varchar(255), PRIMARY KEY (name))");
			openNexusDataStatement.execute();
			openNexusDataStatement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
	}
	
	public synchronized void loadDatabase()
	{
		openConnection();
		
		try
		{		
			//Load players
			PreparedStatement getPlayerDataStatement = connection.prepareStatement("SELECT * FROM player_data");
			ResultSet playerResults = getPlayerDataStatement.executeQuery();

			arenaManager.clearCompetitors();
			
			//HashMap<String, Boolean> tempKits = new HashMap<String, Boolean>();
					
			while (playerResults.next())
			{
				arenaManager.addCompetitor(playerResults.getString("player"),
						new Integer[] {
						playerResults.getInt("wins1"),
						playerResults.getInt("wins2"),
						playerResults.getInt("wins3")},
						
						new Integer[] {
							playerResults.getInt("losses1"),
							playerResults.getInt("losses2"),
							playerResults.getInt("losses3")},
						
						new Integer[] {
							playerResults.getInt("rating1"),
							playerResults.getInt("rating2"),
							playerResults.getInt("rating3")}
						);
			}
			
			getPlayerDataStatement.close();
			playerResults.close();
		
			
			//Load arenas
			PreparedStatement[] getArenaDataStatements = new PreparedStatement[3];
			ResultSet[] arenaResultSets = new ResultSet[3];
			
			getArenaDataStatements[0] = connection.prepareStatement("SELECT * FROM singles_arena_data");
			arenaResultSets[0] = getArenaDataStatements[0].executeQuery();
			
			getArenaDataStatements[1] = connection.prepareStatement("SELECT * FROM doubles_arena_data");
			arenaResultSets[1] = getArenaDataStatements[1].executeQuery();
			
			getArenaDataStatements[2] = connection.prepareStatement("SELECT * FROM triples_arena_data");
			arenaResultSets[2] = getArenaDataStatements[2].executeQuery();
			
			arenaManager.clearArenas();
			
			for (int i = 1; i <= 3; i++)
				while (arenaResultSets[i - 1].next())
				{
					arenaManager.addArena(arenaResultSets[i - 1].getString("name"), i);
					for (int teamNum = 0; teamNum <= 1; teamNum++)
						for (int playerNum = 0; playerNum < i; playerNum++)
							arenaManager.getArena(arenaResultSets[i - 1].getString("name")).setSpawnLocation(teamNum, playerNum, parseLocation(arenaResultSets[i - 1].getString("team" + (teamNum + 1) + "player" + (playerNum + 1))));
					arenaManager.getArena(arenaResultSets[i - 1].getString("name")).setDeathLocation(parseLocation(arenaResultSets[i - 1].getString("deathlocation")));
				}
	
			//Load nexus location
			PreparedStatement getNexusDataStatement = connection.prepareStatement("SELECT * FROM nexus_data");
			ResultSet nexusLocation = getNexusDataStatement.executeQuery();
			String locationRaw;
			if (!nexusLocation.next())
				locationRaw = "null";
			else
				locationRaw = nexusLocation.getString("location");
			 
			arenaManager.setNexusLocation(this.parseLocation(locationRaw));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
	}
	
	public synchronized void saveDatabase()
	{
		openConnection();
		PreparedStatement saveStatement = null;
		try
		{
			//Save competitors
			boolean playerDataExists;
			
			for (OfflinePlayer p : arenaManager.getCompetitors().keySet())
			{
				playerDataExists = isStringInCol("player_data", "player", p.getName(), true);
				
				String query = (playerDataExists ? "UPDATE" : "INSERT INTO") + " player_data SET " +
						"wins1=?, losses1=?, rating1=?, wins2=?, losses2=?, rating2=?, wins3=?, losses3=?, rating3=?" + (playerDataExists ? " WHERE player = '" + p.getName() + "'" : ", player=?");
								
				saveStatement = connection.prepareStatement(query);
				if (!playerDataExists)
					saveStatement.setString(10, p.getName());
				
				saveStatement.setInt(1, arenaManager.getCompetitors().get(p).getWins(GameType.ONE));
				saveStatement.setInt(4, arenaManager.getCompetitors().get(p).getWins(GameType.TWO));
				saveStatement.setInt(7, arenaManager.getCompetitors().get(p).getWins(GameType.THREE));
				
				saveStatement.setInt(2, arenaManager.getCompetitors().get(p).getLosses(GameType.ONE));
				saveStatement.setInt(5, arenaManager.getCompetitors().get(p).getLosses(GameType.TWO));
				saveStatement.setInt(8, arenaManager.getCompetitors().get(p).getLosses(GameType.THREE));
				
				saveStatement.setInt(3, arenaManager.getCompetitors().get(p).getRating(GameType.ONE));
				saveStatement.setInt(6, arenaManager.getCompetitors().get(p).getRating(GameType.TWO));
				saveStatement.setInt(9, arenaManager.getCompetitors().get(p).getRating(GameType.THREE));
				
				if (playerDataExists)
					saveStatement.executeUpdate();
				else
					saveStatement.execute();
			}
			
			//Save arenas
			ArrayList<String> spawnLocations = new ArrayList<String>();
			
			for (String name : arenaManager.getAllArenas().keySet())
			{
				spawnLocations.clear();
				
				Arena tempArena = arenaManager.getAllArenas().get(name);
				//Save spawn locations
				for (int playerNum = 0; playerNum < tempArena.getTeamSize(); playerNum++)
					for (int teamNum = 0; teamNum <= 1; teamNum++)
					{
						if (tempArena.getSpawnLocations()[teamNum][playerNum] == null)
							spawnLocations.add("null");
						else
							spawnLocations.add(locationToString(tempArena.getSpawnLocations()[teamNum][playerNum]));
					}
				
				String arenaType = "";
				if (spawnLocations.size() == 2)
					arenaType = "singles";
				else if (spawnLocations.size() == 4)
					arenaType = "doubles";
				else if (spawnLocations.size() == 6)
					arenaType = "triples";
				
				boolean arenaDataExists = isStringInCol(arenaType + "_arena_data", "name", name, true);
				String query = (arenaDataExists ? "UPDATE" : "INSERT INTO") + " " + arenaType + "_arena_data SET " +
					"team1player1=?, team2player1=?" + (arenaType.equals("doubles") || arenaType.equals("triples") ? ", team1player2=?, team2player2=?" : "") + (arenaType.equals("triples") ? ", team1player3=?, team2player3=?" : "") + ", deathlocation=?" + (arenaDataExists ? " WHERE name=?" : ", name=?");
						
				saveStatement = connection.prepareStatement(query);
				
				for (int i = 0; i < spawnLocations.size(); i++)
					saveStatement.setString(i + 1, spawnLocations.get(i));
				
				saveStatement.setString(spawnLocations.size() + 1, locationToString(tempArena.getDeathLocation()));
				saveStatement.setString(spawnLocations.size() + 2, name);
				
				if (arenaDataExists)
					saveStatement.executeUpdate();
				else
				{
					saveStatement.execute();
				}
				
				//Remove arenas that have been deleted
				PreparedStatement getArenasStatement, deleteArenaStatement;
				String prefixes[] = new String[]{"singles", "doubles", "triples"};

				for (String prefix : prefixes)
				{
					getArenasStatement = connection.prepareStatement("SELECT name FROM " + prefix + "_arena_data");
					ResultSet arenas = getArenasStatement.executeQuery();
					
					while (arenas.next())
					{
						if (arenaManager.getArena(arenas.getString("name")) == null)
						{
							deleteArenaStatement = connection.prepareStatement("DELETE FROM " + prefix + "_arena_data WHERE name = ?");
							deleteArenaStatement.setString(1, arenas.getString("name"));
							
							deleteArenaStatement.execute();						
						}
					}
				}				
			}
			boolean nexusDataExists = isStringInCol("nexus_data", "name", "nexus", true);
			
			String nexusQuery = (nexusDataExists ? "UPDATE" : "INSERT INTO") + " nexus_data SET " +
					"location=?" + (nexusDataExists ? " WHERE name = 'nexus'" : ", name='nexus'");
			
			PreparedStatement nexusStatement = connection.prepareStatement(nexusQuery);
			
			nexusStatement.setString(1, locationToString(this.getArenaManager().getNexusLocation()));
			if (nexusDataExists)
				nexusStatement.executeUpdate();
			else
				nexusStatement.execute();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{		
			closeConnection();
			try
			{
				saveStatement.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private synchronized boolean openConnection()
	{
		String connectionString = "jdbc:mysql://" + this.getConfig().getString("sql.ip") + ":" + this.getConfig().getString("sql.port") + "/" + this.getConfig().getString("sql.database");
		getServer().getLogger().info("Attempting to connect to database: " + connectionString);
		
		try
		{
			connection = DriverManager.getConnection(connectionString, this.getConfig().getString("sql.username"), this.getConfig().getString("sql.password"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private synchronized boolean closeConnection()
	{
		try
		{
			connection.close();
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
	
	public void loadData()
	{
		loadDatabase();
		try
		{
			//Load kit
			ArrayList<ItemStack> armor, inventory;
			armor = new ArrayList<ItemStack>();
			inventory = new ArrayList<ItemStack>();
			
			String[] armorPieces = new String[]{"boots", "legs", "chest", "helmet"};
			
			ItemStack tempStack;

			soupHealAmount = getConfig().getInt("soupheal");
			
			for (String piece : armorPieces)
			{
				try
				{
					tempStack = (new ItemStack(Material.getMaterial(this.getConfig().getString("kit.armor." + piece)), 1));
					
					ConfigurationSection section = getConfig().getConfigurationSection("kit.enchantments." + piece);
					if (section != null)
						for (String enchantName : section.getKeys(false))
						{
							Enchantment enchantment;
							int enchantLevel = -1;
							
							enchantLevel = getConfig().getInt("kit.enchantments." + piece + "." + enchantName);
							enchantment = Enchantment.getByName(enchantName);
							if (enchantLevel != -1 && enchantment != null)
								tempStack.addEnchantment(enchantment, enchantLevel);
						}
					
					armor.add(tempStack);
				}
				catch (NullPointerException e)
				{
					armor.add(new ItemStack(Material.AIR, 1));
				}
			}
			
			for (int slot = 1; slot <= 9; slot++)
			{				
				try
				{
					ConfigurationSection section = getConfig().getConfigurationSection("kit.enchantments." + slot);
					
					tempStack = (new ItemStack(Material.getMaterial(this.getConfig().getString("kit.inventory." + slot)), 1));
					
					if (section != null)
						for (String enchantName : section.getKeys(false))
						{
							Enchantment enchantment;
							int enchantLevel = -1;
							
							enchantLevel = getConfig().getInt("kit.enchantments." + slot + "." + enchantName);
							enchantment = Enchantment.getByName(enchantName);
							if (enchantLevel != -1 && enchantment != null)
								tempStack.addEnchantment(enchantment, enchantLevel);
						}

					inventory.add(tempStack);
					
				}
				catch (NullPointerException e)
				{
					inventory.add(new ItemStack(Material.AIR, 1));
				}
			}
			
			VersusKit.initialize(Arrays.copyOf(inventory.toArray(), inventory.toArray().length, ItemStack[].class), Arrays.copyOf(armor.toArray(), armor.toArray().length, ItemStack[].class));
		}
		catch (NullPointerException e)
		{
			this.getServer().getLogger().info("There are no kits. The plugin will not operate as intended.");
		}
	}
	
	public static String locationToString(Location loc)
	{
		if (loc == null)
		{
			return "null";
		}
		return (loc.getWorld().getName()) + "|" +
				(loc.getBlockX()) + "|" +
				(loc.getBlockY()) + "|" +
				(loc.getBlockZ()) + "|" +
				(loc.getDirection().getX() + "|" +
				(loc.getDirection().getZ()));
	}
	
	public Location parseLocation(String unparsed)
	{		
		if (unparsed == null || unparsed.equalsIgnoreCase("null"))
			return null;
		
		
		String[] coords = unparsed.split("\\|");
		double x, y, z, facingX, facingZ;
		
		World world = this.getServer().getWorld(coords[0]);
		if (world == null)
			return null;
		
		try
		{
			x = Double.parseDouble(coords[1]);
			y = Double.parseDouble(coords[2]);
			z = Double.parseDouble(coords[3]);
			facingX = Double.parseDouble(coords[4]);
			facingZ = Double.parseDouble(coords[5]);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		
		return new Location(world, x, y, z).setDirection(new Vector().setX(facingX).setZ(facingZ));
	}
	
	public ArenaManager getArenaManager()
	{
		return arenaManager;
	}
	
	public void setSelectedLocation(String playerName, Location clickLocation)
	{
		selectedLocations.put(playerName, clickLocation);
	}
	
	public Location getSelectedLocation(String name)
	{
		return selectedLocations.get(name);
	}
	
	public int getSoupHealAmount()
	{
		return soupHealAmount;
	}
}