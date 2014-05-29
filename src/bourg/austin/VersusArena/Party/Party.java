package bourg.austin.VersusArena.Party;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import bourg.austin.VersusArena.MatchmakingEntity;
import bourg.austin.VersusArena.Arena.Competitor;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.Game;
import bourg.austin.VersusArena.Game.VersusTeam;

public class Party implements MatchmakingEntity
{
	private String leader;
	private PartyManager partyManager;
	
	private ArrayList<String> members;
	
	private static int nextID = 0;
	private int partyID;
	
	public Party(String leader, PartyManager partyManager)
	{
		this.partyManager = partyManager;
		
		members = new ArrayList<String>();

		partyID = nextID;
		nextID++;
		
		addPlayer(leader);
		
		setLeader(leader);
		
	}
	
	public String getLeaderName()
	{
		return leader;
	}
	
	public void setLeader(String leaderName)
	{
		leader = leaderName;
		giveLobbyInventory();
	}
	
	public void addPlayer(String playerToAdd)
	{
		partyManager.getPlugin().getArenaManager().removePartyFromQueue(partyID);
		partyManager.getPlugin().getArenaManager().setPlayerStatus(playerToAdd, LobbyStatus.IN_PARTY);
		members.add(playerToAdd);
		
		giveLobbyInventory();
	}
	
	public ArrayList<String> getMembers()
	{
		return members;
	}
	
	public int getID()
	{
		return partyID;
	}
	
	public static int getCurrentID()
	{
		return nextID;
	}
	
	public void broadcast(String message)
	{
		for (String playerName : members)
			partyManager.getPlugin().getServer().getPlayer(playerName).sendMessage(message);
	}
	
	public void broadcast(String message, String player)
	{
		broadcast( ChatColor.BLUE + "[/pc]" + (leader.equals(player) ? ChatColor.GOLD : ChatColor.YELLOW) + "<" + player + "> " + ChatColor.WHITE + message);
	}
	
	public void messageLeader(String message)
	{
		partyManager.getPlugin().getServer().getPlayer(leader).sendMessage(message);
	}
	
	public void playerLeave(String playerName, boolean broadcast)
	{
		partyManager.getPlugin().getArenaManager().removePartyFromQueue(partyID);
		
		Player p = partyManager.getPlugin().getServer().getPlayer(playerName);
		partyManager.getPlugin().getArenaManager().giveLobbyInventory(p);
		partyManager.getPlugin().getArenaManager().setPlayerStatus(playerName, LobbyStatus.IN_LOBBY);
		
		boolean isLeader = leader.equals(playerName);
		members.remove(playerName);
		if (broadcast)
			broadcast(playerName + ChatColor.BLUE + " has left the party.");
		if (members.size() == 0)
		{
			partyManager.deleteParty(partyID);
			return;
		}
		if (isLeader)
		{
			setLeader(members.get(0));
			if (broadcast)
				broadcast(leader + ChatColor.BLUE + " is the new leader.");
		}
	}
	
	public VersusTeam getVersusTeam(Game game)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (String pName : members)
		{
			players.add(game.getGameManager().getArenaManager().getPlugin().getServer().getPlayer(pName));
			if (players.get(players.size() - 1) == null)
				return null;
		}
		
		return new VersusTeam(players, game);
	}
	
	public int getSize()
	{
		return members.size();
	}
	
	public int getRating(GameType type)
	{
		int total = 0;
		for (String name : members)
			total += partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(name)).getRating(type);
		return total / members.size();
	}
	
	public int getRating(LobbyStatus status)
	{
		int total = 0;
		for (String name : members)
			total += partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(name)).getRating(status);
		return total / members.size();
	}
	
	public ArrayList<Competitor> getCompetitors()
	{
		ArrayList<Competitor> list = new ArrayList<Competitor>();
		for (String playerName : members)
			list.add(partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(playerName)));
		
		return list;
	}
	
	public ArrayList<Player> getPlayers()
	{
		ArrayList<Player> list = new ArrayList<Player>();
		for (String playerName : members)
			list.add(partyManager.getPlugin().getServer().getPlayer(playerName));
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public void giveQueueInventory()
	{
		for (String playerName : members)
		{
			if (partyManager.getPlugin().getArenaManager().getPlayerStatus(playerName) != LobbyStatus.IN_GAME)
			{
				Player p = partyManager.getPlugin().getServer().getPlayer(playerName);
				
				p.getInventory().clear();
				p.getInventory().addItem(Inventories.COMPASS);
				if (playerName.equalsIgnoreCase(leader))
				{
					p.getInventory().addItem(Inventories.PARTY_QUEUE);
					p.getInventory().addItem(Inventories.QUEUE_SLOTS[3]);
				}
				
				p.updateInventory();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void giveLobbyInventory()
	{
		for (String playerName : members)
		{
			if (partyManager.getPlugin().getArenaManager().getPlayerStatus(playerName) != LobbyStatus.IN_GAME)
			{
				Player p = partyManager.getPlugin().getServer().getPlayer(playerName);
				
				p.getInventory().clear();
				p.getInventory().addItem(Inventories.COMPASS);
				if (playerName.equalsIgnoreCase(leader)) p.getInventory().addItem(Inventories.PARTY_LOBBY);
				p.updateInventory();
			}
		}
	}
}
