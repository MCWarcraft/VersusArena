package bourg.austin.VersusArena.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

public class VersusMatchmakeTimeTask extends BukkitRunnable
{
	private static int timeToGame = 15;
	
	@Override
	public void run()
	{
		if (timeToGame > 1)
			timeToGame--;
		else
			timeToGame = 15;
	}
	
	public static int getTimeToGame(boolean useTicks)
	{
		return (useTicks? timeToGame * 20 : timeToGame);
	}
}
