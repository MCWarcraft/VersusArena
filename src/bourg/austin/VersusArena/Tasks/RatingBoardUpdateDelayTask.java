package bourg.austin.VersusArena.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.VersusArena;

public class RatingBoardUpdateDelayTask extends BukkitRunnable
{
	private final VersusArena plugin;

	public RatingBoardUpdateDelayTask(VersusArena plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run()
	{
		plugin.getRatingBoards().updateBoards();
	}
}
