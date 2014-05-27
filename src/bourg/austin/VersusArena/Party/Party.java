package bourg.austin.VersusArena.Party;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Game.Game;
import bourg.austin.VersusArena.Game.VersusTeam;

public class Party
{
	private String leader;
	private PartyManager partyManager;
	
	private ArrayList<String> members;
	
	private static int nextID = 0;
	private int partyID;
	
	public Party(String leader, PartyManager partyManager)
	{
		this.leader = leader;
		this.partyManager = partyManager;
		
		members = new ArrayList<String>();
		members.add(leader);
		
		partyID = nextID;
		nextID++;
	}
	
	public String getLeaderName()
	{
		return leader;
	}
	
	public void addPlayer(String playerToAdd)
	{
		members.add(playerToAdd);
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
			leader = members.get(0);
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
}
