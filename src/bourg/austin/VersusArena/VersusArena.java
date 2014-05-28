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
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import bourg.austin.CavemanSQL.DatabaseConnection;
import bourg.austin.CavemanSQL.DatabaseQueryAction;
import bourg.austin.CavemanSQL.DatabaseUpdateAction;
import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Arena.CompetitorManager;
import bourg.austin.VersusArena.Background.MyCommandExecutor;
import bourg.austin.VersusArena.Background.MyListener;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.VersusKit;
import bourg.austin.VersusArena.Party.PartyCommandExecutor;
import bourg.austin.VersusArena.Party.PartyManager;
import bourg.austin.VersusArena.Rating.RatingBoards;

public final class VersusArena extends JavaPlugin
{
	private ArenaManager arenaManager;
	
	private HashMap<String, Location> selectedLocations;
	
	private Connection connection;
	private DatabaseConnection databaseConnection;
	private RatingBoards ratingBoards;
	private CompetitorManager competitorManager;
	private PartyManager partyManager;
	
	private int soupHealAmount;
	
	public void onEnable()
	{		
		databaseConnection = new DatabaseConnection(this.getConfig().getString("sql.ip"), this.getConfig().getString("sql.port"), this.getConfig().getString("sql.database"), this.getConfig().getString("sql.username"), this.getConfig().getString("sql.password"));
		
		openConnection();
		
		this.saveDefaultConfig();
		checkDatabase();
		
		Inventories.initialize();
		ratingBoards = new RatingBoards(this);
		ratingBoards.updateBoards();
		
		competitorManager = new CompetitorManager(this);
		
		//Declare variables
		arenaManager = new ArenaManager(this);
		partyManager = new PartyManager(this);
		selectedLocations = new HashMap<String, Location>();

		//Set event listeners
		this.getServer().getPluginManager().registerEvents(new MyListener(this), this);
		this.getServer().getPluginManager().registerEvents(ratingBoards, this);
		
		PartyCommandExecutor partyExecutor = new PartyCommandExecutor(partyManager);
		
		//Set command executors
		this.getCommand("versus").setExecutor(new MyCommandExecutor(this));
		this.getCommand("party").setExecutor(partyExecutor);
		this.getCommand("pc").setExecutor(partyExecutor);
		this.loadData();
	} 		  
	
	public void onDisable()
	{
		ratingBoards.updateBoards();
		saveDatabase();
	}
	
	public synchronized boolean isStringInCol(String table, String colName, String rowName)
	{
		try
		{
			DatabaseQueryAction action = databaseConnection.getDatabaseQueryAction(table);
			action.addConstraint(colName, rowName);
			ResultSet result = action.executeQuery();
			boolean isInRow = result.next();

			result.close();
			
			return isInRow;
		}
		catch (SQLException e)
		{
			return false;
		}
	}
	
	public synchronized boolean isColInTable(String table, String colName)
	{		
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
	}
	
	private synchronized void checkDatabase()
	{
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
						"kills int DEFAULT 0," +
						"deaths int DEFAULT 0," +
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
	}
	
	public synchronized void loadDatabase()
	{
		try
		{		
			//Load arenas
			DatabaseQueryAction[] getArenaDataStatements = new DatabaseQueryAction[3];
			ResultSet[] arenaResultSets = new ResultSet[3];
			
			getArenaDataStatements[0] = databaseConnection.getDatabaseQueryAction("singles_arena_data");
			arenaResultSets[0] = getArenaDataStatements[0].executeQuery();
			
			getArenaDataStatements[1] = databaseConnection.getDatabaseQueryAction("doubles_arena_data");
			arenaResultSets[1] = getArenaDataStatements[1].executeQuery();
			
			getArenaDataStatements[2] = databaseConnection.getDatabaseQueryAction("triples_arena_data");
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
			DatabaseQueryAction getNexusDataAction = databaseConnection.getDatabaseQueryAction("nexus_data");
			ResultSet nexusLocation = getNexusDataAction.executeQuery();
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
	}
	
	public synchronized void saveDatabase()
	{
		try
		{			
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

				DatabaseUpdateAction updateAction = databaseConnection.getDatabaseUpdateAction(arenaType + "_arena_data");
				
				updateAction.setString("team1player1", spawnLocations.get(0));
				updateAction.setString("team2player1", spawnLocations.get(1));
				if (arenaType.equals("doubles") || arenaType.equals("triples"))
				{
					updateAction.setString("team1player2", spawnLocations.get(2));
					updateAction.setString("team2player2", spawnLocations.get(3));
				}
				if (arenaType.equals("triples"))
				{
					updateAction.setString("team1player3", spawnLocations.get(4));
					updateAction.setString("team2player3", spawnLocations.get(5));
				}
				
				updateAction.setString("deathlocation", locationToString(tempArena.getDeathLocation()));
				updateAction.setPrimaryValue(tempArena.getArenaName());
				
				updateAction.executeUpdate();
				
				//Remove arenas that have been deleted
				PreparedStatement deleteArenaStatement;
				DatabaseQueryAction getArenaAction;
				String prefixes[] = new String[]{"singles", "doubles", "triples"};

				for (String prefix : prefixes)
				{
					getArenaAction = databaseConnection.getDatabaseQueryAction(prefix + "_arena_data");
					//getArenaAction.setFields("*");
					ResultSet arenas = getArenaAction.executeQuery();
					
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
			
			DatabaseUpdateAction nexusAction = databaseConnection.getDatabaseUpdateAction("nexus_data");
			nexusAction.setString("location", locationToString(this.getArenaManager().getNexusLocation()));	
			nexusAction.setPrimaryValue("nexus");
			nexusAction.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized boolean openConnection()
	{
		String connectionString = "jdbc:mysql://" + this.getConfig().getString("sql.ip") + ":" + this.getConfig().getString("sql.port") + "/" + this.getConfig().getString("sql.database");
		
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
	
	public synchronized boolean closeConnection()
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
			ArrayList<PotionEffect> effects;
			
			armor = new ArrayList<ItemStack>();
			inventory = new ArrayList<ItemStack>();
			effects = new ArrayList<PotionEffect>();
			
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
			
			ConfigurationSection potionSection = getConfig().getConfigurationSection("kit.potions");
			for (String potionName : potionSection.getKeys(false))
			{
				int amplifier;
				
				amplifier = getConfig().getInt("kit.potions." + potionName);
				effects.add(new PotionEffect(PotionEffectType.getByName(potionName), 1000, amplifier, false));
			}
			
			VersusKit.initialize(Arrays.copyOf(inventory.toArray(), inventory.toArray().length, ItemStack[].class), Arrays.copyOf(armor.toArray(), armor.toArray().length, ItemStack[].class), Arrays.copyOf(effects.toArray(), effects.toArray().length, PotionEffect[].class));
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
	
	public RatingBoards getRatingBoards()
	{
		return ratingBoards;
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
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public CompetitorManager getCompetitorManager()
	{
		return competitorManager;
	}
	
	public PartyManager getPartyManager()
	{
		return partyManager;
	}
}