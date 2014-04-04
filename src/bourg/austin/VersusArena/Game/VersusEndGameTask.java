package bourg.austin.VersusArena.Game;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VersusEndGameTask extends BukkitRunnable
{
	private final Game game;
	private int losingTeamNum;

	public VersusEndGameTask(Game game, int winningTeamNum)
	{
		this.game = game;
		this.losingTeamNum = winningTeamNum;
	}
	
	@Override
	public void run()
	{
		//Teleport players back to the nexus
		for (int teamNum = 0; teamNum < game.getNumberOfTeams(); teamNum++)
			for (int playerNum = 0; playerNum < game.getTeam(teamNum).getNumberOfPlayers(); playerNum++)
			{
				if (teamNum == losingTeamNum)
					game.getGameManager().getArenaManager().addLoss(game.getTeam(teamNum).getPlayer(playerNum));
				else
					game.getGameManager().getArenaManager().addWin(game.getTeam(teamNum).getPlayer(playerNum));
				game.getGameManager().getArenaManager().bringPlayer(game.getTeam(teamNum).getPlayer(playerNum).getName());
				
				for (Player p : game.getGameManager().getArenaManager().getAllParticipants())
				{
					p.showPlayer(game.getTeam(teamNum).getPlayer(playerNum));
					game.getTeam(teamNum).getPlayer(playerNum).showPlayer(p);
				}
				
			}
		
		game.getGameManager().endGame(game.getGameID());
	}
}
