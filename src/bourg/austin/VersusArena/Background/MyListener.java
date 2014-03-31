package bourg.austin.VersusArena.Background;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.Inventories;

public class MyListener implements Listener
{
	private VersusArena plugin;
	
	public MyListener(VersusArena plugin)
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
		
		//If the action was a right click
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[0].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[0].getItemMeta().getLore()))
				plugin.getArenaManager().addToQueue(event.getPlayer(), 1);
			else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[1].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[1].getItemMeta().getLore()))
				plugin.getArenaManager().addToQueue(event.getPlayer(), 2);
			else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[2].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[2].getItemMeta().getLore()))
				plugin.getArenaManager().addToQueue(event.getPlayer(), 3);
		}
	}
	
	@EventHandler
	public void onItemGrab(PlayerDropItemEvent event)
	{
		if (plugin.getArenaManager().isPlayerInArena(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	@EventHandler
	public void onItemDrop(InventoryClickEvent event)
	{
		if (plugin.getArenaManager().isPlayerInArena(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (plugin.getArenaManager().isPlayerInArena(event.getPlayer().getName()))
			event.setCancelled(true);
	}
}
