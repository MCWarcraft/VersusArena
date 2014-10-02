package bourg.austin.VersusArena.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Tasks.VersusEndGameTask;
import bourg.austin.VersusArena.Tasks.VersusStartGameTask;
import core.Kits.EquippableKitConnector;
import core.Scoreboard.CoreScoreboardManager;
import core.Utilities.HungerStopper;

public class Game implements Listener
{
	private final GameManager gameManager;
	private VersusTeam[] teams;
	private Arena arena;
	
	private static int nextGameID = 0;
	private int gameID;
	private String arenaID;
	
	private List<Player> allPlayers;
	private ArrayList<UUID> quitters;
	
	private GameType gameType;
	
	public Game(GameManager gameManager, List<Player> allPlayers, Arena arena, String arenaID)
	{		
		this.allPlayers = allPlayers;
		quitters = new ArrayList<UUID>();
		
		gameType = GameType.values()[allPlayers.size() / 2 - 1];
		
		this.gameManager = gameManager;
		this.teams = new VersusTeam[]{new VersusTeam(allPlayers.subList(0, allPlayers.size()/2), this), new VersusTeam(allPlayers.subList(allPlayers.size()/2, allPlayers.size()), this)};
		this.arena = arena;
		this.arenaID = arenaID;
		
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
		//Lock players and remove scoreboard
		for (Player player : allPlayers)
		{
			CoreScoreboardManager.getDisplayBoard(player).hide();
			HungerStopper.setCanGetHungry(player.getUniqueId());
			player.sendMessage(ChatColor.BLUE + "Prepare to fight.");
			
			lockPlayer(player);
			//Remove scoreboard
			player.setScoreboard(gameManager.getArenaManager().getPlugin().getServer().getScoreboardManager().getNewScoreboard());
		}
		
		//Teleport
		for (int teamNum = 0; teamNum < 2; teamNum++)
			for (int playerNum = 0; playerNum < teams[teamNum].getNumberOfPlayers(); playerNum++)
				teams[teamNum].getPlayer(playerNum).teleport(arena.getSpawnLocations(arenaID)[teamNum][playerNum]);
		
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
	
	public void quit(UUID leaverUUID)
	{
		quitters.add(leaverUUID);
		
		Player leaver = gameManager.getArenaManager().getPlugin().getServer().getPlayer(leaverUUID);
		
		if (leaver != null)
			for (Player p : gameManager.getArenaManager().getOnlinePlayersInLobby())
			{
				p.showPlayer(leaver);
				leaver.showPlayer(p);
			}
	}
	
	public void broadcast(String message)
	{
		for (Player p : allPlayers)
			if (!quitters.contains(p.getUniqueId()))
				p.sendMessage(message);
	}
	
	public boolean isQuitter(String playerName)
	{
		return quitters.contains(playerName);
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
			EquippableKitConnector.getBaseEquippableKit(p, gameManager.getVersusKitName()).equip();
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
				terminateGame(deathTeam);
				
				return true;
			}
		
		return false;
	}
	
	private void terminateGame(int deathTeam)
	{
		for (Player p : teams[deathTeam].getAllPlayers())
			if (!quitters.contains(p.getUniqueId()))
			{
				//gameManager.getArenaManager().setPlayerStatus(p.getUniqueId(), LobbyStatus.IN_LOBBY);
				p.sendMessage(ChatColor.DARK_RED + "You have lost");
			}
		for (Player p : teams[Math.abs(deathTeam-1)].getAllPlayers())
			if (!quitters.contains(p.getUniqueId()))
			{
				//gameManager.getArenaManager().setPlayerStatus(p.getUniqueId(), LobbyStatus.IN_LOBBY);
				p.sendMessage(ChatColor.GREEN + "You have won");
			}
		new VersusEndGameTask(this, deathTeam).runTaskLater(gameManager.getArenaManager().getPlugin(), 60);
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
	
	public String getArenaID()
	{
		return arenaID;
	}
}
