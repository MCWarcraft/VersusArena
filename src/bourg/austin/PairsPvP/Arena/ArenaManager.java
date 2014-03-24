package bourg.austin.PairsPvP.Arena;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import bourg.austin.PairsPvP.PairsPvP;
import bourg.austin.PairsPvP.Interface.DisplayBoard;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<OfflinePlayer, Competitor> competitors;
	private ArrayList<Player> playersInArena;
	private HashMap<OfflinePlayer, DisplayBoard> boards;
	
	private PairsPvP plugin;
	
	public ArenaManager(PairsPvP plugin)
	{
		this.plugin = plugin;
		arenas = new HashMap<String, Arena>();
		nexusLocation = null;
		
		boards = new HashMap<OfflinePlayer, DisplayBoard>();
		
		competitors = new HashMap<OfflinePlayer, Competitor>();
		playersInArena = new ArrayList<Player>();
	}
	
	public void bringPlayer(String playerName)
	{
		Player player = plugin.getServer().getPlayer(playerName);
		
		//Store data about players in the arena
		playersInArena.add(player);
		
		//Create a new profile
		if (competitors.get(player) == null)
			competitors.put(player, new Competitor(player.getName()));
		
		showLobbyBoard(player);
		
		player.sendMessage(ChatColor.BLUE + "Welcome to 2v2!");
		player.teleport(plugin.getArenaManager().getNexusLocation());
	}
	
	public void showLobbyBoard(Player player)
	{
		ChatColor format = ChatColor.GREEN, titleFormat = ChatColor.AQUA;
		
		Competitor competitor = competitors.get(player);
		
		boards.put(player, new DisplayBoard(player, titleFormat + player.getName()));
		boards.get(player).putField(format + "Wins: ", competitor.getWins());
		boards.get(player).putField(format + "Losses: ", competitor.getLosses());
		boards.get(player).putField(format + "MMR: ", competitor.getMMR());
		boards.get(player).putField(format + "Rating: ", competitor.getRating());
		boards.get(player).display();
	}
	/*
	public void resetPlayer(String playerName)
	{
		if (isPlayerInArena(playerName))
		{
			playersInArena.remove(playerName.toLowerCase());
			try
			{
				plugin.getServer().getPlayer(playerName).teleport(originalPlayerLocations.get(playerName.toLowerCase()));
			}
			catch (NullPointerException e) {}
			
			originalPlayerLocations.remove(playerName.toLowerCase());
		}
	}
	*/
	public HashMap<OfflinePlayer, Competitor> getCompetitors()
	{
		return competitors;
	}
	
	public boolean isPlayerInArena(String name)
	{
		return playersInArena.contains(name.toLowerCase());
	}
	
	public void addCompetitor(String name, int wins, int losses, int mmr, int rating)
	{
		competitors.put(Bukkit.getOfflinePlayer(name), new Competitor(name, wins, losses, mmr, rating));
	}
	
	public void addArena(String name)
	{
		arenas.put(name, new Arena(name));
	}
	
	public void deleteArena(String name)
	{
		arenas.remove(name);
	}
	
	public Arena getArena(String name)
	{
		return arenas.get(name);
	}
	
	public HashMap<String, Arena> getAllArenas()
	{
		return arenas;
	}
	
	public boolean containsArena(String name)
	{
		return arenas.containsKey(name);
	}
	
	public Location getNexusLocation()
	{
		return nexusLocation;
	}
	
	public void setNexusLocation(Location loc)
	{
		nexusLocation = loc;
	}
	
	public void clearArenas()
	{
		arenas.clear();
	}
	
	public void clearCompetitors()
	{
		competitors.clear();
	}
	
}
