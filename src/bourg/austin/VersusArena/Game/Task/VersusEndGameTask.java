package bourg.austin.VersusArena.Game.Task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.HonorPoints.DatabaseOperations;
import bourg.austin.VersusArena.Arena.Competitor;
import bourg.austin.VersusArena.Arena.CompetitorManager;
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
		OfflinePlayer tempPlayer;
		Competitor tempCompetitor;
		
		CompetitorManager tempCompManager = game.getGameManager().getArenaManager().getPlugin().getCompetitorManager();
		
		//Teleport players back to the nexus
		for (int teamNum = 0; teamNum < game.getNumberOfTeams(); teamNum++)
			for (int playerNum = 0; playerNum < game.getTeam(teamNum).getNumberOfPlayers(); playerNum++)
			{
				tempPlayer = Bukkit.getOfflinePlayer(game.getTeam(teamNum).getPlayer(playerNum).getName());
				
				//Add losses or wins
				if (teamNum == losingTeamNum)
				{
					tempCompetitor = tempCompManager.getCompetitor(tempPlayer);
					tempCompetitor.addLoss(game.getGameType());
					tempCompetitor.updateRating(GameResult.LOSS, game.getTeam(Math.abs(teamNum - 1)));
					tempCompManager.updateCompetitor(tempCompetitor);
				}
				else
				{
					tempCompetitor = tempCompManager.getCompetitor(tempPlayer);
					tempCompetitor.addWin(game.getGameType());
					tempCompetitor.updateRating(GameResult.WIN, game.getTeam(Math.abs(teamNum - 1)));
					tempCompManager.updateCompetitor(tempCompetitor);
					
					int money = 0;
					if (game.getGameType() == GameType.ONE)
						money = 10;
					else if (game.getGameType() == GameType.TWO)
						money = 20;
					else if (game.getGameType() == GameType.THREE)
						money = 50;
					else
						System.out.println(ChatColor.RED + "Gametype is invalid. Check VersusEndGameTask");
					
					DatabaseOperations.setCurrency(game.getTeam(teamNum).getPlayer(playerNum), DatabaseOperations.getCurrency(game.getTeam(teamNum).getPlayer(playerNum)) + money);
				}
				
				//If player is offline in the records
				if (game.getGameManager().getArenaManager().getPlayerStatus(game.getTeam(teamNum).getPlayer(playerNum)) != LobbyStatus.OFFLINE)
					game.getGameManager().getArenaManager().bringPlayer(game.getTeam(teamNum).getPlayer(playerNum).getName(), false);
				//If player is still online, heal
				else
					game.getTeam(teamNum).getPlayer(playerNum).setHealth(20);
				
				//Fix invis
				for (Player p : game.getGameManager().getArenaManager().getOnlinePlayersInLobby())
				{
					p.showPlayer(game.getTeam(teamNum).getPlayer(playerNum));
					game.getTeam(teamNum).getPlayer(playerNum).showPlayer(p);
				}
			}
		
		game.getGameManager().endGame(game.getGameID());
		game.getGameManager().getArenaManager().getPlugin().getRatingBoards().updateBoards();
	}
}
