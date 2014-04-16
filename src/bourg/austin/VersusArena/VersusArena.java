package bourg.austin.VersusArena;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Background.MyCommandExecutor;
import bourg.austin.VersusArena.Background.MyListener;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.VersusKits;

public final class VersusArena extends JavaPlugin
{
	private ArenaManager arenaManager;
	
	private HashMap<String, Location> selectedLocations;
	
	private Connection connection;
	
	public void onEnable()
	{		
		checkDatabase();
		
		Inventories.initialize();
		VersusKits.initialize();
		
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
		saveData();
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
						"selectedkit varchar(20) DEFAULT 'Default'," +
						"PRIMARY KEY (player) " +
					")");
			openPlayerDataStatement.execute();
			openPlayerDataStatement.close();
			
			//Configure tables for arenas
			PreparedStatement openSinglesArenaDataStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS singles_arena_data" +
					"( name varchar(17) not null," +
						"team1player1 varchar(255)," +
						"team2player1 varchar(255)," +
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
						"PRIMARY KEY (name) " +
					")");
			openTriplesArenaDataStatement.execute();
			openTriplesArenaDataStatement.close();
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
			PreparedStatement getPlayerDataStatement = connection.prepareStatement("SELECT * FROM VersusArenaData.player_data");
			ResultSet playerResults = getPlayerDataStatement.executeQuery();

			arenaManager.clearCompetitors();
			while (playerResults.next())
			{
				System.out.println(playerResults.getString("selectedkit"));
				
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
							playerResults.getInt("rating3")},
						
						playerResults.getString("selectedkit"));			
			}
			
			getPlayerDataStatement.close();
			playerResults.close();
		
			
			//Load arenas
			PreparedStatement getSinglesDataStatement = connection.prepareStatement("SELECT * FROM VersusArenaData.singles_arena_data");
			ResultSet singlesArenas = getSinglesDataStatement.executeQuery();
			
			PreparedStatement getDoublesDataStatement = connection.prepareStatement("SELECT * FROM VersusArenaData.doubles_arena_data");
			ResultSet doublesArenas = getDoublesDataStatement.executeQuery();
			
			PreparedStatement getTriplesDataStatement = connection.prepareStatement("SELECT * FROM VersusArenaData.triples_arena_data");
			ResultSet triplesArenas = getTriplesDataStatement.executeQuery();
			
			arenaManager.clearArenas();
			//Add singles
			while (singlesArenas.next())
			{
				arenaManager.addArena(singlesArenas.getString("name"), 1);
				for (int teamNum = 0; teamNum <= 1; teamNum++)
					for (int playerNum = 0; playerNum < 1; playerNum++)
						arenaManager.getArena(singlesArenas.getString("name")).setSpawnLocation(teamNum, playerNum, parseLocation(singlesArenas.getString("team" + (teamNum + 1) + "player" + (playerNum + 1))));
			}
			//Add doubles
			while (doublesArenas.next())
			{
				arenaManager.addArena(singlesArenas.getString("name"), 1);
				for (int teamNum = 0; teamNum <= 1; teamNum++)
					for (int playerNum = 0; playerNum < 2; playerNum++)
						arenaManager.getArena(singlesArenas.getString("name")).setSpawnLocation(teamNum, playerNum, parseLocation(singlesArenas.getString("team" + (teamNum + 1) + "player" + (playerNum + 1))));
			}
			//Add triples
			while (triplesArenas.next())
			{
				arenaManager.addArena(singlesArenas.getString("name"), 1);
				for (int teamNum = 0; teamNum <= 1; teamNum++)
					for (int playerNum = 0; playerNum < 3; playerNum++)
						arenaManager.getArena(singlesArenas.getString("name")).setSpawnLocation(teamNum, playerNum, parseLocation(singlesArenas.getString("team" + (teamNum + 1) + "player" + (playerNum + 1))));
			}
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
	
	private synchronized void saveDatabase()
	{
		openConnection();
		PreparedStatement saveStatement = null;
		try
		{			
			//Save competitors
			for (OfflinePlayer p : arenaManager.getCompetitors().keySet())
			{
				System.out.println("Saving " + p.getName());
				
				boolean playerDataExists = isStringInCol("player_data", "player", p.getName(), true);
				
				String query = (playerDataExists ? "UPDATE" : "INSERT INTO") + " player_data SET " +
						"wins1=?, losses1=?, rating1=?, wins2=?, losses2=?, rating2=?, wins3=?, losses3=?, rating3=?, selectedkit=?" + (playerDataExists ? " WHERE player = '" + p.getName() + "'" : ", player=?");
				
				System.out.println(query);
				
				saveStatement = connection.prepareStatement(query);
				if (!playerDataExists)
					saveStatement.setString(11, p.getName());
				
				saveStatement.setInt(1, arenaManager.getCompetitors().get(p).getWins(GameType.ONE));
				saveStatement.setInt(4, arenaManager.getCompetitors().get(p).getWins(GameType.TWO));
				saveStatement.setInt(7, arenaManager.getCompetitors().get(p).getWins(GameType.THREE));
				
				saveStatement.setInt(2, arenaManager.getCompetitors().get(p).getLosses(GameType.ONE));
				saveStatement.setInt(5, arenaManager.getCompetitors().get(p).getLosses(GameType.TWO));
				saveStatement.setInt(8, arenaManager.getCompetitors().get(p).getLosses(GameType.THREE));
				
				saveStatement.setInt(3, arenaManager.getCompetitors().get(p).getRating(GameType.ONE));
				saveStatement.setInt(6, arenaManager.getCompetitors().get(p).getRating(GameType.TWO));
				saveStatement.setInt(9, arenaManager.getCompetitors().get(p).getRating(GameType.THREE));
				
				saveStatement.setString(10, arenaManager.getCompetitors().get(p).getSelectedKitName());
				
				System.out.println("Selected kit " + arenaManager.getCompetitors().get(p).getSelectedKitName());
				
				if (playerDataExists)
					saveStatement.executeUpdate();
				else
					saveStatement.execute();
			}
			
			//Save arenas
			ArrayList<String> spawnLocations = new ArrayList<String>();
			
			for (String name : arenaManager.getAllArenas().keySet())
			{
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
					"team1player1=?, team2player1=?" + (arenaType.equals("doubles") || arenaType.equals("triples") ? ", team1player2=?, team2player2=?" : "") + (arenaType.equals("triples") ? ", team1player3=?, team2player3=?" : "") + (arenaDataExists ? " WHERE name = '" + name + "'" : ", name=?");
				
				System.out.println(query);
						
				saveStatement = connection.prepareStatement(query);
				
				for (int i = 0; i < spawnLocations.size(); i++)
					saveStatement.setString(i + 1, spawnLocations.get(i));
				
				
				if (arenaDataExists)
				{
					System.out.println("updating arena");
					saveStatement.executeUpdate();
				}
				else
				{
					System.out.println("adding new arena");
					saveStatement.setString(spawnLocations.size() + 1, name);
					saveStatement.execute();
				}
			}
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
		String connectionString = "jdbc:mysql://" + this.getConfig().getString("sql.ip") + ":" + this.getConfig().getString("sql.port") + "/VersusArenaData";
		getServer().getLogger().info("Attempting to connect to database: " +connectionString);
		
		try
		{
			connection = DriverManager.getConnection(connectionString, this.getConfig().getString("sql.username"), this.getConfig().getString("sql.password"));
		}
		catch (Exception e)
		{
			System.out.println("There has been an error with the connection. Please check your config.yml file and make sure that the database 'VersusArenaData' exists.");
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
	
	public void saveData()
	{
		saveDatabase();
		
		
		//Save nexus location
		if (this.getArenaManager().getNexusLocation() != null)
			this.getConfig().set("nexus.location", locationToString(this.arenaManager.getNexusLocation()));
		else
			this.getConfig().set("nexus.location", "null");
		
		//Save competitor available kit
		for (OfflinePlayer p : arenaManager.getCompetitors().keySet())
			for (String kitName : arenaManager.getCompetitors().get(p).getAvailableKits().keySet())
				this.getConfig().set("competitors." + p.getName() + ".availablekits." + kitName, arenaManager.getCompetitors().get(p).getAvailableKits().get(kitName));
		
		
		this.saveConfig();
	}
	
	public void loadData()
	{
		loadDatabase();
		//Load nexus location
		try {arenaManager.setNexusLocation(this.parseLocation(this.getConfig().getString("nexus.location")));}
		catch (NullPointerException e) {this.arenaManager.setNexusLocation(null);}
	
		
		
		//Load player kits
		Set<String> competitorNames = null;
		try {competitorNames = this.getConfig().getConfigurationSection("competitors").getKeys(false);}
		catch (NullPointerException e) {}
		
		HashMap<String, Boolean> tempKits;
		
		if (competitorNames != null)
		{
			for (String compName : competitorNames)
			{
				tempKits = new HashMap<String, Boolean>();
				for (String kitName : this.getConfig().getConfigurationSection("competitors." + compName + ".availablekits").getKeys(false))
					tempKits.put(kitName, this.getConfig().getBoolean("competitors." + compName + ".availablekits." + kitName));
					
				arenaManager.setAvailableKits(Bukkit.getOfflinePlayer(compName), tempKits);
			}
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
			System.out.println(coords.length);
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
}