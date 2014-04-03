package bourg.austin.VersusArena.Game;

import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Constants.InGameStatus;

public class VersusUnlockPlayerTask extends BukkitRunnable
{
	private final Game game;

	public VersusUnlockPlayerTask(Game game)
	{
		this.game = game;
	}
	
	@Override
	public void run()
	{
		//Unlock the players by setting them to in game
		for (int teamNum = 0; teamNum < game.getNumberOfTeams(); teamNum++)
			for (int playerNum = 0; playerNum < game.getTeam(teamNum).getNumberOfPlayers(); playerNum++)
			{
				game.getGameManager().setPlayerStatus(game.getTeam(teamNum).getPlayer(playerNum), InGameStatus.ALIVE);
				game.getTeam(teamNum).getPlayer(playerNum).sendMessage("Fight!");
			}
	}
}
