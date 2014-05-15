package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import bourg.austin.HonorPoints.DatabaseOperations;
import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.GameManager;
import bourg.austin.VersusArena.Interface.DisplayBoard;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTask;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTimeTask;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<String, LobbyStatus> playerLobbyStatuses;
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
		
		playerLobbyStatuses = new HashMap<String, LobbyStatus>();
		
		new VersusMatchmakeTask(this).runTaskTimer(this.plugin, 0, 300);
		new VersusMatchmakeTimeTask().runTaskTimer(this.plugin, 20, 20);
	}
	
	public void bringPlayer(String playerName, boolean message)
	{
		Player player = plugin.getServer().getPlayer(playerName);
		player.setHealth(20);
		player.setFireTicks(0);
		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		
		giveLobbyInventory(player);
		
		showLobbyBoard(player);
		
		if (message)
			player.sendMessage(ChatColor.AQUA + "Welcome to the Versus Arena!");
		
		player.teleport(plugin.getArenaManager().getNexusLocation());
		
		//Store data about players in the arena
		playerLobbyStatuses.put(player.getName(), LobbyStatus.IN_LOBBY);
	}
	
	public void removePlayer(Player p)
	{
		boards.remove(p);
		playerLobbyStatuses.remove(p.getName());
	}
	
	public Set<String> getAllParticipantNames()
	{
		return playerLobbyStatuses.keySet();
	}
	
	public void showLobbyBoard(Player player)
	{
		Competitor competitor = plugin.getCompetitorManager().getCompetitor(player);
		
		boards.put(player, new DisplayBoard(player, ChatColor.AQUA + "Versus Arena", ChatColor.GOLD, ChatColor.GREEN));
		
		boards.get(player).putSpace();
		
		boards.get(player).putHeader("[1v1]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.ONE));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.ONE));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.ONE));
		//boards.get(player).putSpace();
		boards.get(player).putHeader("[2v2]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.TWO));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.TWO));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.TWO));
		//boards.get(player).putSpace();
		boards.get(player).putHeader("[3v3]");
		boards.get(player).putField("Rating: ", competitor.getRating(GameType.THREE));
		boards.get(player).putField("Wins: ", competitor.getWins(GameType.THREE));
		boards.get(player).putField("Losses: ", competitor.getLosses(GameType.THREE));
		boards.get(player).putSpace();
		boards.get(player).putField("Honor: ", DatabaseOperations.getCurrency(player));
		
		boards.get(player).display();
	}

	public void addToQueue(Player player, LobbyStatus gameType)
	{
		playerLobbyStatuses.put(player.getName(), gameType);
		giveQueueInventory(player, gameType.getValue());
		player.sendMessage(ChatColor.BLUE + "You are now in the " + gameType.getValue() + "v" + gameType.getValue() + " queue.");
		player.sendMessage(ChatColor.BLUE + "The next set of matches starts in " + ChatColor.GOLD + VersusMatchmakeTimeTask.getTimeToGame(false) + " seconds");
	}
	
	@SuppressWarnings("deprecation")
	public void giveLobbyInventory(Player p)
	{
		p.getInventory().clear();
		
		p.getInventory().addItem(Inventories.COMPASS);
		
		for (ItemStack i : Inventories.LOBBY_SLOTS)
		{
			p.getInventory().addItem(i);
		}
		
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	public void giveQueueInventory(Player p, int type)
	{
		p.getInventory().clear();
		
		p.getInventory().addItem(Inventories.COMPASS);
		
		for (int i = 0; i < Inventories.QUEUE_SLOTS.length; i++)
		{
			if (i != (type - 1) && i < Inventories.LOBBY_SLOTS.length)
				p.getInventory().addItem(Inventories.LOBBY_SLOTS[i]);
			else
				p.getInventory().addItem(Inventories.QUEUE_SLOTS[i]);
		}
		
		p.updateInventory();
	}
	
	public void matchMake(LobbyStatus queueType)
	{
		Arena a = this.getRandomArenaBySize(queueType.getValue());
		
		ArrayList<Player> validPlayers = getSpecificQueue(queueType);
		
		//If there aren't any configured arenas
		if (a == null)
			return;
		
		//If there aren't enough players to start a game
		if (validPlayers.size() < queueType.getValue() * 2)
			return;
		
		//New ArrayList of competitors
		ArrayList<Competitor> validCompetitors = new ArrayList<Competitor>();
		
		//Add the valid players' competitor objects to an ArrayList
		for (Player p : validPlayers)
			validCompetitors.add(plugin.getCompetitorManager().getCompetitor(p));
		
		//Convert to an array
		Competitor[] validCompetitorsArray = validCompetitors.toArray(new Competitor[validCompetitors.size()]);
		
		int n = validCompetitors.size();
        Competitor tempComp;
       
        //Sort competitors
        for(int i  = 0; i < n; i++)
            for(int j = 1; j < (n - i); j++)
                if(validCompetitorsArray[j - 1].getRating(queueType) > validCompetitorsArray[j].getRating(queueType))
                {
                    //swap the elements!
                    tempComp = validCompetitorsArray[j-1];
                    validCompetitorsArray[j-1] = validCompetitorsArray[j];
                    validCompetitorsArray[j] = tempComp;
                }
        
        //Rebuild players in order
        List<Player> sortedValidPlayers = new ArrayList<Player>();
        for (Competitor c : validCompetitorsArray)
        	sortedValidPlayers.add(plugin.getServer().getPlayer(c.getCompetitorName()));
        
        //Drop random players to maintain proper sizing
        int numToDrop = sortedValidPlayers.size() % (queueType.getValue() * 2);
        for (int i = 0; i < numToDrop; i++)
        	sortedValidPlayers.remove((int) (Math.random() * sortedValidPlayers.size()));
		
        //Make games with players
		while (sortedValidPlayers.size() >= queueType.getValue() * 2)
		{
			gameManager.startGame(sortedValidPlayers.subList(0, queueType.getValue() * 2), a);
			sortedValidPlayers = sortedValidPlayers.subList(queueType.getValue() * 2, sortedValidPlayers.size());
		}
	}
	
	public ArrayList<Player> getSpecificQueue(LobbyStatus statusType)
	{
		ArrayList<Player> validPlayers = new ArrayList<Player>();
		
		for (Player p : getOnlinePlayersInLobby())
			if (playerLobbyStatuses.get(p.getName()) == (statusType))
				validPlayers.add(p);
		
		return validPlayers;
	}
	
	public ArrayList<Player> getOnlinePlayersInLobby()
	{
		ArrayList<Player> playersOnline = new ArrayList<Player>();
		
		for (String p : playerLobbyStatuses.keySet())
			if (plugin.getServer().getPlayer(p) != null)
				playersOnline.add(plugin.getServer().getPlayer(p));
		
		return playersOnline;
	}
	
	public void setPlayerStatus(OfflinePlayer player, LobbyStatus status)
	{
		playerLobbyStatuses.put(player.getName(), status);
	}
	
	public HashMap<String, LobbyStatus> getPlayerStatuses()
	{
		return playerLobbyStatuses;
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
		return playerLobbyStatuses.get(p.getName());
	}
	
	public void removeFromQueue(Player p)
	{
		playerLobbyStatuses.put(p.getName(), LobbyStatus.IN_LOBBY);
		this.giveLobbyInventory(p);
		p.sendMessage(ChatColor.BLUE + "You have been removed from the queue");
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

	public void cleanPlayer(Player p)
	{
		boards.remove(p);
		gameManager.getPlayerStatuses().remove(p);
	}
}