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
		HashMap<Player, VersusKit> tempKits = new HashMap<Player, VersusKit>();
		
		for (Player p : players)
		{
			arenaManager.setPlayerStatus(p, LobbyStatus.IN_GAME);
			tempKits.put(p, arenaManager.getPlugin().getVersusKits().getKits().get(arenaManager.getCompetitors().get(p).getSelectedKitName()));
		}
		
		gamesInProgress.put(Game.getNextGameID(), new Game(this, tempKits, a));
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
		ArrayList<Player> tempInArena = new ArrayList<Player>();
		for (Game game : gamesInProgress.values())
			if (game.getArena().getArenaName().equals(name))
				tempInArena.addAll(game.getPlayers());
		
		return tempInArena;
	}
}
