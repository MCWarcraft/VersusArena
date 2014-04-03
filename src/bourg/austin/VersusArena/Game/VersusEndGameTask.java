package bourg.austin.VersusArena.Game;

import org.bukkit.scheduler.BukkitRunnable;

public class VersusEndGameTask extends BukkitRunnable
{
	private final Game game;

	public VersusEndGameTask(Game game)
	{
		this.game = game;
	}
	
	@Override
	public void run()
	{
		//Teleport players back to the nexus
		for (int teamNum = 0; teamNum < game.getNumberOfTeams(); teamNum++)
			for (int playerNum = 0; playerNum < game.getTeam(teamNum).getNumberOfPlayers(); playerNum++)
				game.getGameManager().getArenaManager().bringPlayer(game.getTeam(teamNum).getPlayer(playerNum).getName());
		
		game.getGameManager().endGame(game.getGameID());
		
		System.out.println("Number of active games: " + game.getGameManager().getNumberOfActiveGames());
	}
}
