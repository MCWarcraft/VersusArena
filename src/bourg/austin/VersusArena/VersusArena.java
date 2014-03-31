package bourg.austin.VersusArena;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Background.MyCommandExecutor;
import bourg.austin.VersusArena.Background.MyListener;
import bourg.austin.VersusArena.Constants.Inventories;

public final class VersusArena extends JavaPlugin
{
	private ArenaManager arenaManager;
	
	private HashMap<String, Location> selectedLocations;
	
	public void onEnable()
	{		
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
		saveData();
	}
	
	public void saveData()
	{
		//Save arenas
		HashMap<String, Arena> arenas = arenaManager.getAllArenas();
		this.getConfig().set("arenas", "");
		for (String name : arenas.keySet())
		{
			Arena tempArena = arenas.get(name);
			//Save spawn locations
			for (int teamNum = 0; teamNum <= 1; teamNum++)
				for (int playerNum = 0; playerNum < tempArena.getTeamSize(); playerNum++)
				{
					if (tempArena.getSpawnLocations()[teamNum][playerNum] != null)
					{
						this.getConfig().set("arenas." + name + ".team" + teamNum + ".player" + playerNum, locationToString(tempArena.getSpawnLocations()[teamNum][playerNum]));
					}
					else
					{
						this.getConfig().set("arenas." + name + ".team" + teamNum + ".player" + playerNum, "null");
					}
				}
			this.getConfig().set("arenas." + name + ".teamsize", tempArena.getTeamSize());
		}
		//Save nexus location
		if (this.getArenaManager().getNexusLocation() != null)
			this.getConfig().set("nexus.location", locationToString(this.arenaManager.getNexusLocation()));
		else
			this.getConfig().set("nexus.location", "null");
		
		//Save competitors
		
		for (OfflinePlayer p : arenaManager.getCompetitors().keySet())
		{
			this.getConfig().set("competitors." + p.getName() + ".wins", arenaManager.getCompetitors().get(p).getWins());
			this.getConfig().set("competitors." + p.getName() + ".losses", arenaManager.getCompetitors().get(p).getLosses());
			this.getConfig().set("competitors." + p.getName() + ".rating", arenaManager.getCompetitors().get(p).getRating());
		}
		
		
		this.saveConfig();
	}
	
	public void loadData()
	{
		
		//Load nexus location
		try {arenaManager.setNexusLocation(this.parseLocation(this.getConfig().getString("nexus.location")));}
		catch (NullPointerException e) {this.arenaManager.setNexusLocation(null);}
	
		//Load arenas
		Set<String> arenaNames = null;
		try {arenaNames = this.getConfig().getConfigurationSection("arenas").getKeys(false);}
		catch (NullPointerException e) {}

		if (arenaNames != null)
		{
			arenaManager.clearArenas();
			for (String arenaName : arenaNames)
			{
				int teamSize = this.getConfig().getInt("arenas." + arenaName + ".teamsize");
				arenaManager.addArena(arenaName, teamSize);
				for (int teamNum = 0; teamNum <= 1; teamNum++)
					for (int playerNum = 0; playerNum < teamSize; playerNum++)
					{
						Location tempLoc = this.parseLocation(this.getConfig().getString("arenas." + arenaName + ".team" + teamNum + ".player" + playerNum));
						arenaManager.getArena(arenaName).setSpawnLocation(teamNum, playerNum, tempLoc);
					}
			}
		}
		
		//Load players
		Set<String> competitorNames = null;
		try {competitorNames = this.getConfig().getConfigurationSection("competitors").getKeys(false);}
		catch (NullPointerException e) {}
		
		if (competitorNames != null)
		{
			arenaManager.clearCompetitors();
			for (String compName : competitorNames)
			{
				arenaManager.addCompetitor(compName,
						this.getConfig().getInt("competitors." + compName + ".wins"),
						this.getConfig().getInt("competitors." + compName + ".losses"),
						this.getConfig().getInt("competitors." + compName + ".rating"));
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
				(loc.getBlockZ());
	}
	
	public Location parseLocation(String unparsed)
	{		
		if (unparsed.equalsIgnoreCase("null"))
			return null;
		
		
		String[] coords = unparsed.split("\\|");
		double x, y, z;
		
		World world = this.getServer().getWorld(coords[0]);
		if (world == null)
			return null;
		
		try
		{
			x = Double.parseDouble(coords[1]);
			y = Double.parseDouble(coords[2]);
			z = Double.parseDouble(coords[3]);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		
		return new Location(world, x, y, z);
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