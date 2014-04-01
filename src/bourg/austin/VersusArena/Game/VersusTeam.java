package bourg.austin.VersusArena.Game;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Constants.VersusStatus;

public class VersusTeam
{
	List<Player> players;
	HashMap<Player, VersusStatus> playerStatuses;
	
	public VersusTeam(List<Player> players)
	{
		this.players = players;
		playerStatuses = new HashMap<Player, VersusStatus>();
		
		for (int i = 0; i < this.players.size(); i++)
			playerStatuses.put(players.get(i), VersusStatus.ALIVE);
	}
	
	public boolean isDefeated()
	{
		for (Player p : playerStatuses.keySet())
		{
			if (playerStatuses.get(p).equals(VersusStatus.ALIVE))
				return false;
		}
		return true;
	}
	
	public Player getPlayer(int num)
	{
		try
		{
			return players.get(num);
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public boolean containsPlayer(Player player)
	{
		for (Player p : players)
			if (p.equals(player))
				return true;
		return false;
	}
	
	public List<Player> getAllPlayers()
	{
		return players;
	}
	
	public int getNumberOfPlayers()
	{
		return players.size();
	}
	
	public void setPlayerStatus(Player player, VersusStatus status)
	{
		playerStatuses.put(player, status);
		
		for (Player p : playerStatuses.keySet())
		{
			System.out.println("Status " + p.getName() + status.getValue());
		}
	}
}
