package bourg.austin.VersusArena.Game;

import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.VersusStatus;

public class Game
{
	private final ArenaManager arenaManager;
	private VersusTeam[] teams;
	
	public Game(ArenaManager arenaManager, VersusTeam[] teams)
	{
		this.arenaManager = arenaManager;
		this.teams = teams;
		
		run();
	}
	
	public void run()
	{
		//Lock all players to begin
		for (int teamNum = 0; teamNum < teams.length; teamNum++)
			for (int playerNum = 0; playerNum < teams[teamNum].getNumberOfPlayers(); playerNum++)
			{
				teams[teamNum].getPlayer(playerNum).sendMessage("Locked");
				arenaManager.setPlayerStatus(teams[teamNum].getPlayer(playerNum), VersusStatus.LOCKED);
			}
		
		new VersusUnlockPlayerTask(this).runTaskLater(arenaManager.getPlugin(), 100);
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
