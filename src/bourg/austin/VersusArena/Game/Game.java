package bourg.austin.VersusArena.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import bourg.austin.VersusArena.Arena.Arena;
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
	
	private ArrayList<Player> allPlayers;
	private HashMap<Player, VersusKit> playerKits;
	
	public Game(GameManager gameManager, HashMap<Player, VersusKit> playerKits, Arena arena)
	{		
		
		
		allPlayers = new ArrayList<Player>();
		allPlayers.addAll(playerKits.keySet());
		
		System.out.println("Game has been created with:");
		for (Player p : allPlayers)
			System.out.println(p.getName());
		
		for (Player p : playerKits.keySet())
		{
			System.out.println(p.getName() + ((playerKits.get(p) == null) ? " has no kit" : "has a kit"));
		}
		
		this.playerKits = playerKits;
		
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
		//Register events starting on run
		//gameManager.getArenaManager().getPlugin().getServer().getPluginManager().registerEvents(this, gameManager.getArenaManager().getPlugin());
		
		System.out.println("Game is being run");
		
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
				System.out.println("Invis call InGame " + playerInGame.getName() + " InArena " + playerInArena.getName());
				
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
				System.out.println("Invis fix p1 " + player1.getName() + " p2 " + player2.getName());
				player1.showPlayer(player2);
				player2.showPlayer(player1);
			}
		}
	}
	
	public void distributeKits()
	{
		System.out.println("Kits should be given to " + allPlayers.size() + " players:");
		for (Player p : allPlayers)
		{
			System.out.println("Kit given to " + p.getName());
			playerKits.get(p).equipToPlayer(p);
		}
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
	
	public boolean checkGameOver(int deathTeam)
	{
		if (teams[deathTeam].isDefeated())
		{
			for (Player p : allPlayers)
				p.sendMessage(ChatColor.BLUE + "It's all over!");
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
}
