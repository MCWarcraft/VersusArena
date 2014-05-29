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
	
	public void startGame(List<Player> team1, List<Player> team2, Arena a)
	{
		for (Player p : team1)
		{
			arenaManager.setPlayerStatus(p.getName(), LobbyStatus.IN_GAME);
			p.setHealth(20);
		}
		for (Player p : team2)
		{
			arenaManager.setPlayerStatus(p.getName(), LobbyStatus.IN_GAME);
			p.setHealth(20);
		}
		
		ArrayList<Player> all = new ArrayList<Player>();
		for (Player p : team1)
			all.add(p);
		for (Player p : team2)
			all.add(p);
		
		gamesInProgress.put(Game.getNextGameID(), new Game(this, all, a));
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
		/*
		for (Player p : gamesInProgress.get(id).getPlayers())
		{
			if (arenaManager.getPlayerStatus(p) == null)
				arenaManager.removePlayer(p);
		}
		*/
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
}
