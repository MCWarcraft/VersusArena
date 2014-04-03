package bourg.austin.VersusArena.Game;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Constants.InGameStatus;

public class Game implements Listener
{
	private final GameManager gameManager;
	private VersusTeam[] teams;
	private Arena arena;
	
	private static int nextGameID = 0;
	private int gameID;
	
	private List<Player> allPlayers;
	
	public Game(GameManager gameManager, List<Player> players, Arena arena)
	{		
		this.gameManager = gameManager;
		this.teams = new VersusTeam[]{new VersusTeam(players.subList(0, players.size()/2), this), new VersusTeam(players.subList(players.size()/2, players.size()), this)};
		this.arena = arena;
		
		gameID = nextGameID;
		nextGameID++;
				
		allPlayers = players;
		
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
		
		//Set invisibility
		setInGameVisibility();
		
		//Lock players and remove scoreboard
		for (Player player : allPlayers)
		{
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
		
		new VersusUnlockPlayerTask(this).runTaskLater(gameManager.getArenaManager().getPlugin(), 60);
	}
	
	/*
	@EventHandler
	public void onPlayerDeath(EntityDamageEvent event)
	{
		System.out.println("Player death event in game ID " + gameID);
		
		if (event.getEntity() == null)
			return;
		else if (!(event.getEntity() instanceof Player))
			return;

		Player involvedPlayer = (Player) event.getEntity();
		
		if (involvedPlayer == null)
			return;
		if (!allPlayers.contains(involvedPlayer))
			return;
		if (involvedPlayer.getHealth() - event.getDamage() > 0)
			return;
		
		event.setCancelled(true);
		involvedPlayer.setHealth(20);
		
		int playerTeamNum = -1;
		
		if (teams[0].containsPlayer(involvedPlayer))
			playerTeamNum = 0;
		else
			playerTeamNum = 1;
		
		//Hide the player from sight
		for (Player p : allPlayers)
			if (!involvedPlayer.equals(p))
				p.hidePlayer(involvedPlayer);
		
		gameManager.setPlayerStatus(involvedPlayer, InGameStatus.DEAD);
		
		for (Player p : allPlayers)
			p.sendMessage(ChatColor.BLUE + involvedPlayer.getName() + " has fallen!");
		
		if (teams[playerTeamNum].isDefeated())
		{
			for (Player p : allPlayers)
				p.sendMessage(ChatColor.BLUE + "It's all over!");
			for (Player p : teams[playerTeamNum].getAllPlayers())
				p.sendMessage(ChatColor.DARK_RED + "You have lost");
			for (Player p : teams[Math.abs(playerTeamNum-1)].getAllPlayers())
				p.sendMessage(ChatColor.GREEN + "You have won");
			
			new VersusEndGameTask(this).runTaskLater(gameManager.getArenaManager().getPlugin(), 60);
		}
	}
	*/
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
	
	public int getNumberOfTeams()
	{
		return teams.length;
	}
	
	public List<Player> getPlayers()
	{
		return allPlayers;
	}
}
