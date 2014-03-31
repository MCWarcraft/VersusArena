package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Interface.DisplayBoard;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<OfflinePlayer, Competitor> competitors;
	private ArrayList<OfflinePlayer> playersInArena;
	private HashMap<OfflinePlayer, DisplayBoard> boards;
	
	private HashMap<Player, Integer> queue;
	
	private VersusArena plugin;
	
	public ArenaManager(VersusArena plugin)
	{
		this.plugin = plugin;
		arenas = new HashMap<String, Arena>();
		nexusLocation = null;
		
		boards = new HashMap<OfflinePlayer, DisplayBoard>();
		
		queue = new HashMap<Player, Integer>();
		
		competitors = new HashMap<OfflinePlayer, Competitor>();
		playersInArena = new ArrayList<OfflinePlayer>();
	}
	public void bringPlayer(String playerName)
	{		
		Player player = plugin.getServer().getPlayer(playerName);
		
		//Store data about players in the arena
		playersInArena.add(player);
		
		giveLobbyInventory(player);
		
		//Create a new profile
		if (competitors.get(player) == null)
			competitors.put(player, new Competitor(player.getName()));
		
		showLobbyBoard(player);
		
		player.sendMessage(ChatColor.BLUE + "Welcome to the Versus Arena!");
		player.teleport(plugin.getArenaManager().getNexusLocation());
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
	public boolean addToQueue(Player player, int gameType)
	{
		if (!(gameType > 0 || gameType <= 3))
			return false;
		else if (queue.containsKey(player))
			return false;
		else
		{
			queue.put(player, gameType);
			giveQueueInventory(player, gameType);
			player.sendMessage(ChatColor.BLUE + "You are now in the " + gameType + "v" + gameType + " queue.");
			return true;
		}
	}
	
	public void giveLobbyInventory(Player p)
	{
		p.sendMessage("" + Inventories.LOBBY_SLOTS[0].getItemMeta().getLore());
		
		p.getInventory().clear();
		for (ItemStack i : Inventories.LOBBY_SLOTS)
			p.getInventory().addItem(i);
	}
	
	public void giveQueueInventory(Player p, int type)
	{
		p.sendMessage("" + type);
		
		p.getInventory().clear();
		for (int i = 0; i < Inventories.LOBBY_SLOTS.length; i++)
		{
			if (i != (type - 1))
				p.getInventory().addItem(Inventories.LOBBY_SLOTS[i]);
			else
				p.getInventory().addItem(Inventories.QUEUE_SLOTS[i]);
		}
	}
	
	public HashMap<OfflinePlayer, Competitor> getCompetitors()
	{
		return competitors;
	}
	
	public boolean isPlayerInArena(String name)
	{
		return playersInArena.contains(Bukkit.getOfflinePlayer(name));
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
