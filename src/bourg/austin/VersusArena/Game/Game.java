package bourg.austin.VersusArena.Game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.VersusStatus;

public class Game implements Listener
{
	private final ArenaManager arenaManager;
	private VersusTeam[] teams;
	private Arena arena;
	
	private ArrayList<Player> allPlayers;
	
	public Game(ArenaManager arenaManager, VersusTeam[] teams, Arena arena)
	{
		this.arenaManager = arenaManager;
		this.teams = teams;
		this.arena = arena;
		
		run();
	}
	
	public void run()
	{
		allPlayers = new ArrayList<Player>();
		
		arenaManager.getPlugin().getServer().getPluginManager().registerEvents(this, arenaManager.getPlugin());
		
		//Lock all players and set invis to begin
		for (int teamNum = 0; teamNum < teams.length; teamNum++)
			for (int playerNum = 0; playerNum < teams[teamNum].getNumberOfPlayers(); playerNum++)
			{
				allPlayers.add(teams[teamNum].getPlayer(playerNum));
			}
		//Exceptions to invis (AKA players in game)
		for (Player player : allPlayers)
		{
			//Lock
			arenaManager.setPlayerStatus(player, VersusStatus.LOCKED);
			
			//Invis for all except those in game with the user
			for (Player onlinePlayer : arenaManager.getPlayersInGame())
			{
				if (!allPlayers.contains(onlinePlayer))
					player.hidePlayer(onlinePlayer);
				else
					player.showPlayer(onlinePlayer);
			}
			
			//Remove scoreboard
			player.setScoreboard(arenaManager.getPlugin().getServer().getScoreboardManager().getNewScoreboard());
		}
		
		//Teleport
		for (int teamNum = 0; teamNum < teams.length; teamNum++)
			for (int playerNum = 0; playerNum < teams[teamNum].getNumberOfPlayers(); playerNum++)
			{
				teams[teamNum].getPlayer(playerNum).teleport(arena.getSpawnLocations()[teamNum][playerNum]);
			}
		
		new VersusUnlockPlayerTask(this).runTaskLater(arenaManager.getPlugin(), 60);
	}
	
	@EventHandler
	public void onPlayerDeath(EntityDamageEvent event)
	{
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
		
		teams[playerTeamNum].setPlayerStatus(involvedPlayer, VersusStatus.DEAD);
		
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
			
			new VersusEndGameTask(this).runTaskLater(arenaManager.getPlugin(), 60);
		}
	}
	
	public ArenaManager getArenaManager()
	{
		return arenaManager;
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
}
