package bourg.austin.VersusArena.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Constants.LobbyStatus;

public class GameManager
{
	private ArenaManager arenaManager;
	
	private HashMap<Player, InGameStatus> playerInGameStatuses;
	private HashMap<Integer, Game> gamesInProgress;
	
	public GameManager(ArenaManager arenaManager)
	{
		this.arenaManager = arenaManager;
		playerInGameStatuses = new HashMap<Player, InGameStatus>();
		gamesInProgress = new HashMap<Integer, Game>();
	}
	
	public Game getGameByParticipant(Player p)
	{
		for (Game game : gamesInProgress.values())
			if (game.getPlayers().contains(p))
				return game;
		return null;
	}
	
	public void startGame(List<Player> players, Arena a)
	{
		for (Player p : players)
			arenaManager.setPlayerStatus(p, LobbyStatus.IN_GAME);
		
		gamesInProgress.put(Game.getNextGameID(), new Game(this, players, a));
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
	
	public void endGame(int id)
	{
		gamesInProgress.remove(id);
	}
	
	public ArrayList<Player> getPlayersInArena(String name)
	{ 
		ArrayList<Player> tempInArena = new ArrayList<Player>();
		for (Game game : gamesInProgress.values())
			if (game.getArena().getArenaName().equals(name))
				tempInArena.addAll(game.getPlayers());
		
		return tempInArena;
	}
	
	//TODO Delete this testing code
	public int getNumberOfActiveGames()
	{
		return gamesInProgress.keySet().size();
	}
	
}
