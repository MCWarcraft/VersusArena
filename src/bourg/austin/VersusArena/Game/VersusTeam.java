package bourg.austin.VersusArena.Game;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Constants.InGameStatus;

public class VersusTeam
{
	List<Player> players;
	Game game;
	
	public VersusTeam(List<Player> players, Game game)
	{
		this.players = players;
		this.game = game;
	}
	
	public boolean isDefeated()
	{
		HashMap<Player, InGameStatus> statuses = game.getGameManager().getPlayerStatuses();
		
		for (Player p : statuses.keySet())
			if (players.contains(p) && statuses.get(p).equals(InGameStatus.ALIVE))
				return false;
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
	
	public int getAverageRating()
	{
		int totalRating = 0;
		for (Player p : players)
			totalRating += game.getGameManager().getArenaManager().getCompetitors().get(p).getRating();
		return totalRating / players.size();
	}
}
