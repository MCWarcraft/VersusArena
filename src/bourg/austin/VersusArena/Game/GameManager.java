package bourg.austin.VersusArena.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.InGameStatus;

public class GameManager
{
	private ArenaManager arenaManager;
	
	private HashMap<Player, InGameStatus> playerInGameStatuses;
	private ArrayList<Game> gamesInProgress;
	
	public GameManager(ArenaManager arenaManager)
	{
		this.arenaManager = arenaManager;
		playerInGameStatuses = new HashMap<Player, InGameStatus>();
		gamesInProgress = new ArrayList<Game>();
	}
	
	public void startGame(List<Player> players, Arena a)
	{
		gamesInProgress.add(new Game(this, players, a));
	}
	
	public InGameStatus getPlayerStatus(Player p)
	{
		return playerInGameStatuses.get(p);
	}
	
	public void setPlayerStatus(Player p, InGameStatus status)
	{
		playerInGameStatuses.put(p, status);
	}
	
	public HashMap<Player, InGameStatus> getPlayerStatuses()
	{
		return playerInGameStatuses;
	}
	
	public ArenaManager getArenaManager()
	{
		return arenaManager;
	}
	
	public ArrayList<Player> getPlayersInArena(String name)
	{ 
		ArrayList<Player> tempInArena = new ArrayList<Player>();
		for (Game game : gamesInProgress)
			if (game.getArena().getArenaName().equals(name))
				tempInArena.addAll(game.getPlayers());
		
		return tempInArena;
	}
	
	
}
