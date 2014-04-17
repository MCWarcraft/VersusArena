package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.GameManager;
import bourg.austin.VersusArena.Interface.DisplayBoard;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<OfflinePlayer, Competitor> competitors;
	private HashMap<Player, LobbyStatus> playerLobbyStatuses;
	private HashMap<OfflinePlayer, DisplayBoard> boards;
	
	private GameManager gameManager;
	private VersusArena plugin;
	
	public ArenaManager(VersusArena plugin)
	{
		this.plugin = plugin;
		arenas = new HashMap<String, Arena>();
		nexusLocation = null;
		
		gameManager = new GameManager(this);
		
		boards = new HashMap<OfflinePlayer, DisplayBoard>();
		
		competitors = new HashMap<OfflinePlayer, Competitor>();
		playerLobbyStatuses = new HashMap<Player, LobbyStatus>();
	}
	
	public void bringPlayer(String playerName)
	{		
		Player player = plugin.getServer().getPlayer(playerName);
		
		giveLobbyInventory(player);
		
		//Create a new profile
		if (competitors.get(player) == null)
			competitors.put(player, new Competitor(player.getName(), plugin));
		
		showLobbyBoard(player);
		
		player.sendMessage(ChatColor.BLUE + "Welcome to the Versus Arena!");
		player.teleport(plugin.getArenaManager().getNexusLocation());
		
		//Store data about players in the arena
		playerLobbyStatuses.put(player, LobbyStatus.IN_LOBBY);
	}
	
	public void removePlayer(Player p)
	{
		boards.remove(p);
		playerLobbyStatuses.remove(p);
		System.out.println(p.getName() + " has been removed from the arena");
	}
	
	public void setAvailableKits(OfflinePlayer p, HashMap<String, Boolean> kits)
	{
		System.out.println("Player: " + p.getName());
		System.out.println("Kits length: " + kits.size());
		Competitor tempCompetitor = competitors.get(p);
		if (tempCompetitor == null)
			return;
		tempCompetitor.setAvailableKits(kits);
		competitors.put(p, tempCompetitor);
	}
	
	public void setSelectedKit(OfflinePlayer p, String kitName)
	{
		Competitor tempCompetitor = competitors.get(p);
		if (tempCompetitor == null)
			return;
		tempCompetitor.setSelectedKitName(kitName);
		competitors.put(p, tempCompetitor);
	}
	
	public Set<Player> getAllParticipants()
	{
		return playerLobbyStatuses.keySet();
	}
	
	public void showLobbyBoard(Player player)
	{		
		Competitor competitor = competitors.get(player);
		
		boards.put(player, new DisplayBoard(player, ChatColor.AQUA + "Versus Arena", ChatColor.GOLD, ChatColor.GREEN));
		
		boards.get(player).putSpace();
		
		boards.get(player).putHeader("[1v1]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.ONE));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.ONE));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.ONE));
		
		boards.get(player).putSpace();
		
		boards.get(player).putHeader("[2v2]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.TWO));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.TWO));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.TWO));
		
		boards.get(player).putSpace();
		
		boards.get(player).putHeader("[3v3]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.THREE));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.THREE));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.THREE));
		
		boards.get(player).display();
	}
	
	public void addWin(OfflinePlayer player, GameType type)
	{
		competitors.put(player, competitors.get(player).addWin(type));
	}
	
	public void addLoss(OfflinePlayer player, GameType type)
	{
		competitors.put(player, competitors.get(player).addLoss(type));
	}

	public boolean addToQueue(Player player, LobbyStatus gameType)
	{
		if (!(gameType.getValue() > 0 || (gameType.getValue() <= 3)))
		{
			player.sendMessage("error1");
			return false;
		}
		
		else
		{
			playerLobbyStatuses.put(player, gameType);
			giveQueueInventory(player, gameType.getValue());
			player.sendMessage(ChatColor.BLUE + "You are now in the " + gameType.getValue() + "v" + gameType.getValue() + " queue.");
			
			matchMake(gameType);
			
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void giveLobbyInventory(Player p)
	{
		p.getInventory().clear();
		for (ItemStack i : Inventories.LOBBY_SLOTS)
		{
			System.out.println("ID " + i.getType().getId());
			p.getInventory().addItem(i);
		}
		
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
	
	public void matchMake(LobbyStatus statusType)
	{
		ArrayList<Player> validPlayers = getSpecificQueue(statusType);
		
		Arena a = this.getRandomArenaBySize(statusType.getValue());
		if (a == null)
			return;
		
		
		
		if (validPlayers.size() >= statusType.getValue() * 2)
		{
			System.out.println("startgame executed");
			gameManager.startGame(validPlayers.subList(0, statusType.getValue() * 2), a);
		}
	}
	
	
	
	public ArrayList<Player> getSpecificQueue(LobbyStatus statusType)
	{
		ArrayList<Player> validPlayers = new ArrayList<Player>();
		
		for (Player p : playerLobbyStatuses.keySet())
			if (playerLobbyStatuses.get(p).equals(statusType))
				validPlayers.add(p);
		
		return validPlayers;
	}
	
	public void setPlayerStatus(Player player, LobbyStatus status)
	{
		playerLobbyStatuses.put(player,  status);
	}
	
	public HashMap<OfflinePlayer, Competitor> getCompetitors()
	{
		return competitors;
	}
	
	public ArrayList<Arena> getArenasBySize(int size)
	{
		ArrayList<Arena> validArenas = new ArrayList<Arena>();
		boolean none = true;
		for (String s : arenas.keySet())
			if (arenas.get(s).getTeamSize() == size && arenas.get(s).isConfigured())
			{
				validArenas.add(arenas.get(s));
				none = false;
			}
		if (none)
			return null;
		return validArenas;
	}
	
	public Arena getRandomArenaBySize(int size)
	{
		ArrayList<Arena> validArenas = getArenasBySize(size);
		if (validArenas == null)
			return null;
		return validArenas.get((int)(Math.random() * validArenas.size()));
	}
	
	public LobbyStatus getPlayerStatus(OfflinePlayer p)
	{
		return playerLobbyStatuses.get(p);
	}
	
	public ArrayList<Player> getPlayersInGame()
	{
		ArrayList<Player> tempList = new ArrayList<Player>();
		
		for (Player p : playerLobbyStatuses.keySet())
			if (playerLobbyStatuses.get(p).equals(LobbyStatus.IN_GAME))
				tempList.add(p);
		return tempList;
	}
	
	public void removeFromQueue(Player p)
	{
		playerLobbyStatuses.put(p, LobbyStatus.IN_LOBBY);
		this.giveLobbyInventory(p);
		p.sendMessage(ChatColor.BLUE + "You have been removed from the queue");
	}
	
	public void addCompetitor(String name, Integer[] wins, Integer[] losses, Integer[] rating, String selectedKitName)
	{
		competitors.put(Bukkit.getOfflinePlayer(name), new Competitor(name, wins, losses, rating, selectedKitName, plugin));
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
	
	public VersusArena getPlugin()
	{
		return plugin;
	}
	
	public GameManager getGameManager()
	{
		return gameManager;
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