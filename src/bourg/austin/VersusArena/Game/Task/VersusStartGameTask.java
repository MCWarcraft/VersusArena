package bourg.austin.VersusArena.Game.Task;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Game.Game;

public class VersusStartGameTask extends BukkitRunnable
{
	private final Game game;
	private int countdown;
	
	public VersusStartGameTask(Game game, int countdown)
	{
		this.game = game;
		this.countdown = countdown;
		for (Player p : game.getPlayers())
			p.sendMessage(ChatColor.GOLD + "The match is starting in:");
	}
	
	@Override
	public void run()
	{
		for (Player p : game.getPlayers())
			p.sendMessage(ChatColor.GOLD + "" + (countdown > 0 ? countdown : "Fight!"));
		countdown--;
		
		if (countdown < 0)
		{
			//Unlock the players by setting them to in game
			for (int teamNum = 0; teamNum < game.getNumberOfTeams(); teamNum++)
				for (int playerNum = 0; playerNum < game.getTeam(teamNum).getNumberOfPlayers(); playerNum++)
				{
					System.out.println("Unlocking " + game.getTeam(teamNum).getPlayer(playerNum).getName());
					
					game.getGameManager().setPlayerStatus(game.getTeam(teamNum).getPlayer(playerNum), InGameStatus.ALIVE);
				}
			this.cancel();
		}

	}
}
