package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import bourg.austin.HonorPoints.DatabaseOperations;
import bourg.austin.VersusArena.MatchmakingEntity;
import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.GameManager;
import bourg.austin.VersusArena.Interface.DisplayBoard;
import bourg.austin.VersusArena.Party.Party;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTask;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTimeTask;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<String, LobbyStatus> playerLobbyStatuses;
	private ArrayList<Integer> partyInQueue;
	
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
		partyInQueue = new ArrayList<Integer>();
		
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
		
		//Store data about players in the arena
		
		
		if (plugin.getPartyManager().isInParty(player.getName()))
		{
			playerLobbyStatuses.put(player.getName(), LobbyStatus.IN_PARTY);
			plugin.getPartyManager().getParty(player.getName()).giveLobbyInventory();
		}
		else
		{
			playerLobbyStatuses.put(player.getName(), LobbyStatus.IN_LOBBY);
			giveLobbyInventory(player);
		}
		
		showLobbyBoard(player);
		
		if (message)
			player.sendMessage(ChatColor.AQUA + "Welcome to the Versus Arena!");
		
		player.teleport(plugin.getArenaManager().getNexusLocation());
	}
	
	public void addPartyToQueue(int id)
	{
		partyInQueue.add(id);
		plugin.getPartyManager().getParty(id).giveQueueInventory();
		
		Party party = plugin.getPartyManager().getParty(id);
		
		party.broadcast(ChatColor.BLUE + "You are now in the " + party.getSize() + "v" + party.getSize() + " queue.");
		party.broadcast(ChatColor.BLUE + "The next set of matches starts in " + ChatColor.GOLD + VersusMatchmakeTimeTask.getTimeToGame(false) + " seconds");
	}
	
	public void removePartyFromQueue(int id)
	{
		partyInQueue.remove(new Integer(id));
		try{plugin.getPartyManager().getParty(id).giveLobbyInventory();} catch(NullPointerException e){}
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
	
	private int trueSize(ArrayList<MatchmakingEntity> entities)
	{
		int playersInQueue = 0;
		for (MatchmakingEntity e : entities)
			playersInQueue += e.getSize();
		
		return playersInQueue;
	}
	
	public void matchMake(LobbyStatus queueType)
	{
		Arena a = this.getRandomArenaBySize(queueType.getValue());
		
		ArrayList<Player> validPlayers = getSpecificQueue(queueType);
		
		//If there aren't any configured arenas
		if (a == null)
			return;
		
		//New ArrayList of competitors
		ArrayList<MatchmakingEntity> validMatchmakingEntities = new ArrayList<MatchmakingEntity>();
		
		//Add the valid players' competitor objects to an ArrayList
		for (Player p : validPlayers)
			validMatchmakingEntities.add(plugin.getCompetitorManager().getCompetitor(p));
		
		//Add the parties from the party queue of proper size
		for (int id : partyInQueue)
		{
			Party tempParty = plugin.getPartyManager().getParty(id);
			if (tempParty.getSize() == queueType.getValue())
				validMatchmakingEntities.add(tempParty);
		}
		
		int playersInQueue = trueSize(validMatchmakingEntities);
		
		//If there aren't enough players to start a game
		if (playersInQueue < queueType.getValue() * 2)
			return;
		
		//Convert to an array
		MatchmakingEntity[] validMatchmakingEntitiesArray = validMatchmakingEntities.toArray(new MatchmakingEntity[validMatchmakingEntities.size()]);
		
		int n = validMatchmakingEntities.size();
        MatchmakingEntity tempEntity;
       
        //Sort entities
        for(int i  = 0; i < n; i++)
            for(int j = 1; j < (n - i); j++)
                if(validMatchmakingEntitiesArray[j - 1].getRating(queueType) > validMatchmakingEntitiesArray[j].getRating(queueType))
                {
                    //swap the elements!
                    tempEntity = validMatchmakingEntitiesArray[j-1];
                    validMatchmakingEntitiesArray[j-1] = validMatchmakingEntitiesArray[j];
                    validMatchmakingEntitiesArray[j] = tempEntity;
                }
        
        //Turn back to an arraylist
        ArrayList<MatchmakingEntity> sortedMatchmakingEntities = new ArrayList<MatchmakingEntity>();
		for (MatchmakingEntity e : validMatchmakingEntitiesArray)
        	sortedMatchmakingEntities.add(e);
        
        //Drop random players to maintain proper sizing
        int numToDrop = playersInQueue % (queueType.getValue() * 2);
        while (numToDrop != 0)
        {
        	int indexToDrop = ((int) (Math.random() * sortedMatchmakingEntities.size()));
        	if (sortedMatchmakingEntities.get(indexToDrop).getSize() <= numToDrop)
        		sortedMatchmakingEntities.remove(indexToDrop);
        }
        
        
        //While there are enough players to make a team
		while (trueSize(sortedMatchmakingEntities) >= queueType.getValue() * 2)
		{			
			ArrayList<ArrayList<Player>> players = new ArrayList<ArrayList<Player>>();
			players.add(new ArrayList<Player>());
			players.add(new ArrayList<Player>());
			int attemptIndex = 0;
			
			while (players.get(0).size() < queueType.getValue() || players.get(1).size() < queueType.getValue())
			{
				for (int i = 0; i < 2; i++)
				{
					if (players.get(i).size() + sortedMatchmakingEntities.get(attemptIndex).getSize() <= queueType.getValue())
					{
						players.get(i).addAll(sortedMatchmakingEntities.get(attemptIndex).getPlayers());
						if (sortedMatchmakingEntities.get(attemptIndex) instanceof Party)
							removePartyFromQueue(((Party) sortedMatchmakingEntities.get(attemptIndex)).getID());
						sortedMatchmakingEntities.remove(attemptIndex);
						i = 3;
					}
					else if (i == 1)
						attemptIndex =+ 1;
				}
			}
			gameManager.startGame(players.get(0), players.get(1), a);
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