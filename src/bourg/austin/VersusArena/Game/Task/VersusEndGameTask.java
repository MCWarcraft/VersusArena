package bourg.austin.VersusArena.Game.Task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.HonorPoints.DatabaseOperations;
import bourg.austin.VersusArena.Constants.GameResult;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.Game;

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
				//Add wins
				if (teamNum == losingTeamNum)
				{
					game.getGameManager().getArenaManager().addLoss(game.getTeam(teamNum).getPlayer(playerNum), game.getGameType());
					game.getGameManager().getArenaManager().getCompetitors().get(game.getTeam(teamNum).getPlayer(playerNum)).updateRating(GameResult.LOSS, game.getTeam(Math.abs(teamNum - 1)));
				}
				else
				{
					game.getGameManager().getArenaManager().addWin(game.getTeam(teamNum).getPlayer(playerNum), game.getGameType());
					game.getGameManager().getArenaManager().getCompetitors().get(game.getTeam(teamNum).getPlayer(playerNum)).updateRating(GameResult.WIN, game.getTeam(Math.abs(teamNum - 1)));
					DatabaseOperations.setCurrency(game.getTeam(teamNum).getPlayer(playerNum), DatabaseOperations.getCurrency(game.getTeam(teamNum).getPlayer(playerNum)) + (game.getGameType().equals(GameType.ONE) ? 10 : 50));
				}
				
				//If player is offline in the records
				if (!game.getGameManager().getArenaManager().getPlayerStatus(game.getTeam(teamNum).getPlayer(playerNum)).equals(LobbyStatus.OFFLINE))
					game.getGameManager().getArenaManager().bringPlayer(game.getTeam(teamNum).getPlayer(playerNum).getName());
				//If player is still online, heal
				else
					game.getTeam(teamNum).getPlayer(playerNum).setHealth(20);
				
				for (Player p : game.getGameManager().getArenaManager().getAllParticipants())
				{
					p.showPlayer(game.getTeam(teamNum).getPlayer(playerNum));
					game.getTeam(teamNum).getPlayer(playerNum).showPlayer(p);
				}
			}
		
		game.getGameManager().endGame(game.getGameID());
	}
}
