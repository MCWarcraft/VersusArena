package bourg.austin.VersusArena.Arena;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.VersusStatus;
import bourg.austin.VersusArena.Interface.DisplayBoard;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<OfflinePlayer, Competitor> competitors;
	private HashMap<OfflinePlayer, VersusStatus> playerStatuses;
	private HashMap<OfflinePlayer, DisplayBoard> boards;
	
	private VersusArena plugin;
	
	public ArenaManager(VersusArena plugin)
	{
		this.plugin = plugin;
		arenas = new HashMap<String, Arena>();
		nexusLocation = null;
		
		boards = new HashMap<OfflinePlayer, DisplayBoard>();
		
		competitors = new HashMap<OfflinePlayer, Competitor>();
		playerStatuses = new HashMap<OfflinePlayer, VersusStatus>();
	}
	
	public void bringPlayer(String playerName)
	{		
		Player player = plugin.getServer().getPlayer(playerName);
		
		giveLobbyInventory(player);
		
		//Create a new profile
		if (competitors.get(player) == null)
			competitors.put(player, new Competitor(player.getName()));
		
		showLobbyBoard(player);
		
		player.sendMessage(ChatColor.BLUE + "Welcome to the Versus Arena!");
		player.teleport(plugin.getArenaManager().getNexusLocation());
		
		//Store data about players in the arena
		playerStatuses.put(player, VersusStatus.IN_LOBBY);
	}
	
	public void showLobbyBoard(Player player)
	{		
		Competitor competitor = competitors.get(player);
		
		boards.put(player, new DisplayBoard(player, ChatColor.DARK_AQUA + player.getName(), ChatColor.GOLD, ChatColor.BLUE));
		boards.get(player).putField("Wins: ", competitor.getWins());
		boards.get(player).putField("Losses: ", competitor.getLosses());
		boards.get(player).putField("Rating: ", competitor.getRating());
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
	public boolean addToQueue(Player player, VersusStatus gameType)
	{
		if (!(gameType.getValue() > 0 || (gameType.getValue() <= 3)))
		{
			player.sendMessage("Err1");
			return false;
		}
		
		else
		{
			playerStatuses.put(player, gameType);
			giveQueueInventory(player, gameType.getValue());
			player.sendMessage(ChatColor.BLUE + "You are now in the " + gameType.getValue() + "v" + gameType.getValue() + " queue.");
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void giveLobbyInventory(Player p)
	{
		p.getInventory().clear();
		for (ItemStack i : Inventories.LOBBY_SLOTS)
			p.getInventory().addItem(i);
		
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	public void giveQueueInventory(Player p, int type)
	{
		p.getInventory().clear();
		for (int i = 0; i < Inventories.QUEUE_SLOTS.length; i++)
		{
			if (i != (type - 1) && i < Inventories.LOBBY_SLOTS.length)
				p.getInventory().addItem(Inventories.LOBBY_SLOTS[i]);
			else
				p.getInventory().addItem(Inventories.QUEUE_SLOTS[i]);
		}
		
		p.updateInventory();
	}
	
	public HashMap<OfflinePlayer, Competitor> getCompetitors()
	{
		return competitors;
	}
	
	public VersusStatus getPlayerStatus(OfflinePlayer p)
	{
		return playerStatuses.get(p);
	}
	
	public void removeFromQueue(Player p)
	{
		playerStatuses.put(p, VersusStatus.IN_LOBBY);
		this.giveLobbyInventory(p);
		p.sendMessage(ChatColor.BLUE + "You have been removed from the queue");
	}
	
	public void addCompetitor(String name, int wins, int losses, int rating)
	{
		competitors.put(Bukkit.getOfflinePlayer(name), new Competitor(name, wins, losses, rating));
	}
	
	public void addArena(String name, int teamSize)
	{
		arenas.put(name, new Arena(name, teamSize));
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
