package bourg.austin.VersusArena.Game;

import org.bukkit.entity.Player;

public class VersusTeam
{
	Player[] players;
	
	public VersusTeam(Player[] players)
	{
		this.players = players;
	}
	
	public Player getPlayer(int num)
	{
		try
		{
			return players[num];
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public int getNumberOfPlayers()
	{
		return players.length;
	}
}
