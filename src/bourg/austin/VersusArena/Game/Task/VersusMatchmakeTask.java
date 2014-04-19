package bourg.austin.VersusArena.Game.Task;

import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Arena.ArenaManager;
import bourg.austin.VersusArena.Constants.LobbyStatus;

public class VersusMatchmakeTask extends BukkitRunnable
{	
	private ArenaManager arenaManager;
	
	public VersusMatchmakeTask(ArenaManager arenaManager)
	{
		this.arenaManager = arenaManager;
	}
	
	@Override
	public void run()
	{
		arenaManager.matchMake(LobbyStatus.IN_1V1_QUEUE);
		arenaManager.matchMake(LobbyStatus.IN_2V2_QUEUE);
		arenaManager.matchMake(LobbyStatus.IN_3V3_QUEUE);
	}
}
