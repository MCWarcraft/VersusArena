package bourg.austin.PairsPvP.Background;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import bourg.austin.PairsPvP.PairsPvP;

public class MyListener implements Listener
{
	private PairsPvP plugin;
	
	public MyListener(PairsPvP plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event)
	{
		//If player is holding a stick
		if (event.getPlayer().getItemInHand().getType().equals(Material.STICK) && event.getPlayer().hasPermission("pairspvp.select"))
		{
			//If the action was a right click
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				//Store the click
				plugin.setSelectedLocation(event.getPlayer().getName(), event.getClickedBlock().getLocation());
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Location Selected");

			}
		}
	}
}
