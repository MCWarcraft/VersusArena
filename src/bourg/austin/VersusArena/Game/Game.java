package bourg.austin.VersusArena.Game;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Constants.VersusKit;
import bourg.austin.VersusArena.Game.Task.VersusEndGameTask;
import bourg.austin.VersusArena.Game.Task.VersusStartGameTask;

public class Game implements Listener
{
	private final GameManager gameManager;
	private VersusTeam[] teams;
	private Arena arena;
	
	private static int nextGameID = 0;
	private int gameID;
	
	private List<Player> allPlayers;
	
	private GameType gameType;
	
	public Game(GameManager gameManager, List<Player> allPlayers, Arena arena)
	{		
		this.allPlayers = allPlayers;
		
		gameType = GameType.values()[allPlayers.size() / 2 - 1];
		
		this.gameManager = gameManager;
		this.teams = new VersusTeam[]{new VersusTeam(allPlayers.subList(0, allPlayers.size()/2), this), new VersusTeam(allPlayers.subList(allPlayers.size()/2, allPlayers.size()), this)};
		this.arena = arena;
		
		gameID = nextGameID;
		nextGameID++;
				
		run();
	}
	
	public int getGameID()
	{
		return gameID;
	}
	
	public static int getNextGameID()
	{
		return nextGameID;
	}
	
	public void run()
	{
		//Set invisibility
		setInGameVisibility();
		
		//Lock players and remove scoreboard
		for (Player player : allPlayers)
		{
			player.sendMessage(ChatColor.BLUE + "Prepare to fight.");
			
			lockPlayer(player);
			//Remove scoreboard
			player.setScoreboard(gameManager.getArenaManager().getPlugin().getServer().getScoreboardManager().getNewScoreboard());
		}
		
		//Teleport
		for (int teamNum = 0; teamNum < 2; teamNum++)
			for (int playerNum = 0; playerNum < teams[teamNum].getNumberOfPlayers(); playerNum++)
			{
				teams[teamNum].getPlayer(playerNum).teleport(arena.getSpawnLocations()[teamNum][playerNum]);
			}
		
		distributeKits();
		
		new VersusStartGameTask(this, 3).runTaskTimer(gameManager.getArenaManager().getPlugin(), 0, 20);
	}
	
	public GameManager getGameManager()
	{
		return gameManager;
	}
	
	private void lockPlayer(Player player)
	{
		gameManager.setPlayerStatus(player, InGameStatus.LOCKED);
	}
	
	private void setInGameVisibility()
	{
		for (Player playerInGame : allPlayers)
		{
			for (Player playerInArena : gameManager.getPlayersInArena(arena.getArenaName()))
			{
				if (!allPlayers.contains(playerInArena))
				{
					playerInArena.hidePlayer(playerInGame);
					playerInGame.hidePlayer(playerInArena);
				}
				else
				{
					playerInArena.showPlayer(playerInGame);
					playerInGame.showPlayer(playerInArena);
				}
			}
		}
		
		for (Player player1 : allPlayers)
		{
			for (Player player2 : allPlayers.subList(1, allPlayers.size()))
			{
				player1.showPlayer(player2);
				player2.showPlayer(player1);
			}
		}
	}
	
	public boolean areTeammates(Player p1, Player p2)
	{
		for (VersusTeam team : teams)
			if (team.containsPlayer(p1) && team.containsPlayer(p2))
				return true;
		return false;
	}
	
	public void distributeKits()
	{
		for (Player p : allPlayers)
			VersusKit.equipToPlayer(p);
	}
	
	public Arena getArena()
	{
		return arena;
	}
	
	public VersusTeam getTeam(int teamNum)
	{
		try
		{
			return teams[teamNum];
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public boolean checkGameOver()
	{		
		for (int deathTeam = 0; deathTeam < 2; deathTeam++)
			if (teams[deathTeam].isDefeated())
			{
				for (Player p : teams[deathTeam].getAllPlayers())
					p.sendMessage(ChatColor.DARK_RED + "You have lost");
				for (Player p : teams[Math.abs(deathTeam-1)].getAllPlayers())
					p.sendMessage(ChatColor.GREEN + "You have won");
				
				new VersusEndGameTask(this, deathTeam).runTaskLater(gameManager.getArenaManager().getPlugin(), 60);
				
				return true;
			}
		
		return false;
	}
	
	public int getNumberOfTeams()
	{
		return teams.length;
	}
	
	public int getTeamNum(Player p)
	{
		if (teams[0].containsPlayer(p))
			return 0;
		else if (teams[1].containsPlayer(p))
			return 0;
		return -1;
	}
	
	public List<Player> getPlayers()
	{
		return allPlayers;
	}
	
	public GameType getGameType()
	{
		return gameType;
	}
}
