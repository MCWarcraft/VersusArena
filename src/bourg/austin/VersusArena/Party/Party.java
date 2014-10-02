package bourg.austin.VersusArena.Party;

import java.util.ArrayList;
import java.util.UUID;

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
import core.Utilities.CoreItems;
import core.Utilities.CoreUtilities;

public class Party implements MatchmakingEntity
{
	private UUID leaderUUID;
	private PartyManager partyManager;
	
	private ArrayList<UUID> members;
	
	private static int nextID = 0;
	private int partyID;
	
	public Party(UUID leaderUUID, PartyManager partyManager)
	{
		this.partyManager = partyManager;
		
		members = new ArrayList<UUID>();

		partyID = nextID;
		nextID++;
		
		addPlayer(leaderUUID);
		
		setLeader(leaderUUID);
		
	}
	
	public UUID getLeaderUUID()
	{
		return leaderUUID;
	}
	
	public void setLeader(UUID leaderUUID)
	{
		this.leaderUUID = leaderUUID;
		giveLobbyInventory();
	}
	
	public void addPlayer(UUID playerUUIDToAdd)
	{
		partyManager.getPlugin().getArenaManager().removePartyFromQueue(partyID);
		partyManager.getPlugin().getArenaManager().setPlayerStatus(playerUUIDToAdd, LobbyStatus.IN_PARTY);
		members.add(playerUUIDToAdd);
		
		giveLobbyInventory();
	}
	
	public ArrayList<UUID> getMemberUUIDs()
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
		for (UUID playerUUID : members)
			partyManager.getPlugin().getServer().getPlayer(playerUUID).sendMessage(message);
	}
	
	public void broadcast(String message, UUID playerUUID)
	{
		broadcast( ChatColor.BLUE + "[/pc]" + (leaderUUID.equals(playerUUID) ? ChatColor.GOLD : ChatColor.YELLOW) + "<" + Bukkit.getPlayer(playerUUID).getName() + "> " + ChatColor.WHITE + message);
	}
	
	public void messageLeader(String message)
	{
		partyManager.getPlugin().getServer().getPlayer(leaderUUID).sendMessage(message);
	}
	
	public void playerLeave(UUID playerUUID, boolean broadcast)
	{
		partyManager.getPlugin().getArenaManager().removePartyFromQueue(partyID);
		
		Player p = partyManager.getPlugin().getServer().getPlayer(playerUUID);
		partyManager.getPlugin().getArenaManager().giveLobbyInventory(p);
		partyManager.getPlugin().getArenaManager().setPlayerStatus(playerUUID, LobbyStatus.IN_LOBBY);
		
		boolean isLeader = leaderUUID.equals(playerUUID);
		members.remove(playerUUID);
		if (broadcast)
			broadcast(p.getName() + ChatColor.BLUE + " has left the party.");
		if (members.size() == 0)
		{
			partyManager.deleteParty(partyID);
			return;
		}
		if (isLeader)
		{
			setLeader(members.get(0));
			if (broadcast)
				broadcast(Bukkit.getPlayer(leaderUUID).getName() + ChatColor.BLUE + " is the new leader.");
		}
	}
	
	public VersusTeam getVersusTeam(Game game)
	{
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (UUID pUUID : members)
		{
			players.add(game.getGameManager().getArenaManager().getPlugin().getServer().getPlayer(pUUID));
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
		for (UUID playerUUID : members)
			total += partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(playerUUID)).getRating(type);
		return total / members.size();
	}
	
	public int getRating(LobbyStatus status)
	{
		int total = 0;
		for (UUID playerUUID : members)
			total += partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(playerUUID)).getRating(status);
		return total / members.size();
	}
	
	public ArrayList<Competitor> getCompetitors()
	{
		ArrayList<Competitor> list = new ArrayList<Competitor>();
		for (UUID playerUUID : members)
			list.add(partyManager.getPlugin().getCompetitorManager().getCompetitor(Bukkit.getOfflinePlayer(playerUUID)));
		
		return list;
	}
	
	public ArrayList<Player> getPlayers()
	{
		ArrayList<Player> list = new ArrayList<Player>();
		for (UUID playerUUID : members)
			list.add(partyManager.getPlugin().getServer().getPlayer(playerUUID));
		
		return list;
	}
	
	@SuppressWarnings("deprecation")
	public void giveQueueInventory()
	{
		for (UUID playerUUID : members)
		{
			if (partyManager.getPlugin().getArenaManager().getPlayerStatus(playerUUID) != LobbyStatus.IN_GAME)
			{
				Player p = partyManager.getPlugin().getServer().getPlayer(playerUUID);
				
				CoreUtilities.resetPlayerState(p, false);
				p.getInventory().addItem(CoreItems.COMPASS);
				if (playerUUID.equals(leaderUUID))
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
		for (UUID playerUUID : members)
		{
			if (partyManager.getPlugin().getArenaManager().getPlayerStatus(playerUUID) != LobbyStatus.IN_GAME)
			{
				Player p = partyManager.getPlugin().getServer().getPlayer(playerUUID);
				
				CoreUtilities.resetPlayerState(p, false);
				p.getInventory().addItem(CoreItems.COMPASS);
				if (playerUUID.equals(leaderUUID)) p.getInventory().addItem(Inventories.PARTY_LOBBY);
				p.updateInventory();
			}
		}
	}
}
