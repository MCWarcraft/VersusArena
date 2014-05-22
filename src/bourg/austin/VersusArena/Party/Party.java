package bourg.austin.VersusArena.Party;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Game.Game;
import bourg.austin.VersusArena.Game.VersusTeam;

public class Party
{
	private String leader;
	private ArrayList<String> members;
	
	private static int id = 0;
	private int partyID;
	
	public Party(String leader)
	{
		this.leader = leader;
		
		members = new ArrayList<String>();
		members.add(leader);
		
		partyID = id;
		id++;
	}
	
	public String getLeaderName()
	{
		return leader;
	}
	
	public void addPlayer(String playerToAdd)
	{
		members.add(playerToAdd);
	}
	
	public void removePlayer(String playerToRemove)
	{
		members.remove(playerToRemove);
	}
	
	public ArrayList<String> getMembers()
	{
		return members;
	}
	
	public int getID()
	{
		return partyID;
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
