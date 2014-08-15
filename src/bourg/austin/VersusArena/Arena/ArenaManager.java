package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import bourg.austin.VersusArena.MatchmakingEntity;
import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.GameManager;
import bourg.austin.VersusArena.Party.Party;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTask;
import bourg.austin.VersusArena.Tasks.VersusMatchmakeTimeTask;
import core.Custody.Custody;
import core.HonorPoints.HonorConnector;
import core.Scoreboard.CoreScoreboardManager;
import core.Scoreboard.DisplayBoard;
import core.Utilities.CoreItems;

public class ArenaManager
{
	private HashMap<String, Arena> arenas;
	private Location nexusLocation;
	
	private HashMap<String, LobbyStatus> playerLobbyStatuses;
	private ArrayList<Integer> partyInQueue;
	
	private GameManager gameManager;
	private VersusArena plugin;
	
	private HonorConnector honorConnector;
	
	public ArenaManager(VersusArena plugin)
	{
		this.plugin = plugin;
		arenas = new HashMap<String, Arena>();
		nexusLocation = null;
		
		gameManager = new GameManager(this, plugin.getConfig().getString("kitname"));
		
		playerLobbyStatuses = new HashMap<String, LobbyStatus>();
		partyInQueue = new ArrayList<Integer>();
		
		honorConnector = new HonorConnector();
		
		new VersusMatchmakeTask(this).runTaskTimer(this.plugin, 0, 300);
		new VersusMatchmakeTimeTask().runTaskTimer(this.plugin, 20, 20);
	}
	
	public void bringPlayer(String playerName, boolean entry)
	{
		if (entry)
			Custody.switchCustody(plugin.getServer().getPlayer(playerName), "arena");
		
		Player player = plugin.getServer().getPlayer(playerName);
		player.setHealth(20);
		player.setFireTicks(0);
		
		//Clear armor
		player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		
		if (entry)
		{
			generateLobbyBoard(player);
			player.sendMessage(ChatColor.AQUA + "Welcome to the Arena!");
		}
		
		player.teleport(plugin.getArenaManager().getNexusLocation());
		
		CoreScoreboardManager.getDisplayBoard(player).update(true);
		
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
		playerLobbyStatuses.remove(p.getName());
	}
	
	public Set<String> getAllParticipantNames()
	{
		return playerLobbyStatuses.keySet();
	}
	
	public void generateLobbyBoard(Player player)
	{
		Competitor comp = plugin.getCompetitorManager().getCompetitor(player);
		DisplayBoard tempBoard = CoreScoreboardManager.getDisplayBoard(player);
		
		tempBoard.setScoreColor(ChatColor.GOLD);
		tempBoard.setTitle("Welcome to the Arena " + player.getName() + "!", "" + ChatColor.AQUA);
		
		tempBoard.putSpace();
		tempBoard.putHeader(ChatColor.GREEN + "[1v1]");
		tempBoard.putField("Rating: ", comp, "rating1");
		tempBoard.putField("Wins: ", comp, "wins1");
		tempBoard.putField("Losses: ", comp, "losses1");
		
		tempBoard.putHeader(ChatColor.GREEN + "[2v2]");
		tempBoard.putField("Rating: ", comp, "rating2");
		tempBoard.putField("Wins: ", comp, "wins2");
		tempBoard.putField("Losses: ", comp, "losses2");

		tempBoard.putHeader(ChatColor.GREEN + "[3v3]");
		tempBoard.putField("Rating: ", comp, "rating3");
		tempBoard.putField("Wins: ", comp, "wins3");
		tempBoard.putField("Losses: ", comp, "losses3");
		tempBoard.putSpace();
		
		tempBoard.putField("Honor: ", honorConnector, player.getName());
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
		
		p.getInventory().addItem(CoreItems.COMPASS);
		
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
		
		p.getInventory().addItem(CoreItems.COMPASS);
		
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
		//Get players for the queue type
		ArrayList<Player> validPlayers = getSpecificQueue(queueType);
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
		
		//The number of players between singles and parties
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
        int maxPlayers = getNumAvailableArenas(queueType) * (queueType.getValue() * 2);
        
        if (maxPlayers == 0)
        	return;
        
        if (playersInQueue - numToDrop > maxPlayers)
        	numToDrop = playersInQueue - maxPlayers;
        
        while (numToDrop != 0)
        {
        	int indexToDrop = ((int) (Math.random() * sortedMatchmakingEntities.size()));
        	if (sortedMatchmakingEntities.get(indexToDrop).getSize() <= numToDrop)
        	{
        		numToDrop -= sortedMatchmakingEntities.get(indexToDrop).getSize();
        		sortedMatchmakingEntities.remove(indexToDrop);
        	}
        }
        
        //While there are enough players to make a team
		while (trueSize(sortedMatchmakingEntities) >= queueType.getValue() * 2)
		{			
			ArrayList<ArrayList<Player>> players = new ArrayList<ArrayList<Player>>();
			players.add(new ArrayList<Player>());
			players.add(new ArrayList<Player>());
			int attemptIndex = 0;
			
			//While team 1 size is less than team amount needed OR team 2 size is less than team amount needed
			while (players.get(0).size() < queueType.getValue() || players.get(1).size() < queueType.getValue())
			{
				for (int i = 0; i < 2; i++)
				{
					
					//Get first team with the same queue size
					if (players.get(i).size() + sortedMatchmakingEntities.get(attemptIndex).getSize() <= queueType.getValue())
					{
						players.get(i).addAll(sortedMatchmakingEntities.get(attemptIndex).getPlayers());
						if (sortedMatchmakingEntities.get(attemptIndex) instanceof Party)
							removePartyFromQueue(((Party) sortedMatchmakingEntities.get(attemptIndex)).getID());
						sortedMatchmakingEntities.remove(attemptIndex);
						
						//Break loop
						i = 3;
					}
					else if (i == 1)
						attemptIndex =+ 1;
				}
			}
			
			Arena a = this.getRandomArenaBySize(queueType.getValue());
			
			//If there aren't any configured arenas
			if (a == null)
				return;
			
			String arenaID = a.checkoutInstance();
			
			if (arenaID == null)
				continue;
			
			//Start game with teams
			gameManager.startGame(players.get(0), players.get(1), a, arenaID);
		}
	}
	
	public int getNumAvailableArenas(LobbyStatus statusType)
	{
		ArrayList<Arena> arenas = this.getArenasBySize(statusType.getValue());
		
		int avail = 0;
		
		for (Arena a : arenas)
			avail += a.getNumAvailableInstances();
		
		return avail;
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
	
	public void setPlayerStatus(String playerName, LobbyStatus status)
	{
		playerLobbyStatuses.put(playerName, status);
	}
	
	public LobbyStatus getPlayerStatus(String playerName)
	{
		return playerLobbyStatuses.get(playerName);
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

	public void removeFromQueue(Player p)
	{
		playerLobbyStatuses.put(p.getName(), LobbyStatus.IN_LOBBY);
		this.giveLobbyInventory(p);
		p.sendMessage(ChatColor.BLUE + "You have been removed from the queue");
	}
	
	public void addArena(Arena arena)
	{
		arenas.put(arena.getArenaName(), arena);
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
		gameManager.getPlayerStatuses().remove(p);
	}
}