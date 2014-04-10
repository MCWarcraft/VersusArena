package bourg.austin.VersusArena.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Constants.VersusKit;
import bourg.austin.VersusArena.Constants.VersusKits;

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
		System.out.println("Now in startgame");
		
		HashMap<Player, VersusKit> tempKits = new HashMap<Player, VersusKit>();
		
		for (Player p : players)
		{
			arenaManager.setPlayerStatus(p, LobbyStatus.IN_GAME);
			tempKits.put(p, VersusKits.getKits().get(arenaManager.getCompetitors().get(p).getSelectedKitName()));
		}
		
		gamesInProgress.put(Game.getNextGameID(), new Game(this, tempKits, a));
		System.out.println("Startgame finished");
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
		for (Player p : gamesInProgress.get(id).getPlayers())
		{
			if (arenaManager.getPlayerStatus(p).equals(LobbyStatus.OFFLINE));
				arenaManager.removePlayer(p);
		}
		gamesInProgress.remove(id);
	}
	
	public ArrayList<Player> getPlayersInArena(String name)
	{ 
		System.out.println("Getting players in arena");
		ArrayList<Player> tempInArena = new ArrayList<Player>();
		for (Game game : gamesInProgress.values())
		{
			System.out.println("Checking " + name + " against " + game.getArena().getArenaName());
			if (game.getArena().getArenaName().equals(name))
				tempInArena.addAll(game.getPlayers());
		}
		
		System.out.println("There are " + tempInArena.size() + "players sharing the arena");
		return tempInArena;
	}
	
	//TODO Delete this testing code
	public int getNumberOfActiveGames()
	{
		return gamesInProgress.keySet().size();
	}
	
}
