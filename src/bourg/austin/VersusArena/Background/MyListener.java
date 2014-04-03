package bourg.austin.VersusArena.Background;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;

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
		if (event.getPlayer().getItemInHand().getType().equals(Material.STICK) && event.getPlayer().hasPermission("pairspvp.select") && event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			//Store the click
			plugin.setSelectedLocation(event.getPlayer().getName(), event.getClickedBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.YELLOW + "Location Selected");
		}
		
		//If the action was a right click on a sign with first line "/versus" and the player has permission
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && ((event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))) && ((Sign) event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "/versus") && event.getPlayer().hasPermission("pairspvp.arena.go"))
		{
			if (plugin.getArenaManager().getNexusLocation() != null)
			{
				plugin.getArenaManager().bringPlayer(event.getPlayer().getName());
				return;
			}
			//If no nexus exists
			else
				event.getPlayer().sendMessage(ChatColor.RED + "There is currently no PvP nexus. Please notify the mods.");
		}
		
		else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			try
			{				
				if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[0].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[0].getItemMeta().getLore()))
					plugin.getArenaManager().addToQueue(event.getPlayer(), LobbyStatus.IN_1V1_QUEUE);
				else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[1].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[1].getItemMeta().getLore()))
					plugin.getArenaManager().addToQueue(event.getPlayer(), LobbyStatus.IN_2V2_QUEUE);
				else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.LOBBY_SLOTS[2].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.LOBBY_SLOTS[2].getItemMeta().getLore()))
					plugin.getArenaManager().addToQueue(event.getPlayer(), LobbyStatus.IN_3V3_QUEUE);
				if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.QUEUE_SLOTS[3].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.QUEUE_SLOTS[3].getItemMeta().getLore()))
					plugin.getArenaManager().removeFromQueue(event.getPlayer());
				
			}
			catch (NullPointerException e)
			{
				
			}
		}
	}
	
	@EventHandler
	public void onItemGrab(PlayerDropItemEvent event)
	{
		if (plugin.getArenaManager().getPlayerStatus(event.getPlayer()) != null)
			event.setCancelled(true);
	}
	@EventHandler
	public void onItemDrop(InventoryClickEvent event)
	{
		if (plugin.getArenaManager().getPlayerStatus((Player) event.getWhoClicked()) != null)
			event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (plugin.getArenaManager().getPlayerStatus(event.getPlayer()) != null)
		{
			event.setCancelled(true);
			event.getPlayer().updateInventory();
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		if (!event.getLine(0).equalsIgnoreCase("/versus"))
			return;
		else if (event.getPlayer().hasPermission("pairspvp.nexus.sign"))
		{
				event.setLine(0, ChatColor.DARK_BLUE + event.getLine(0));
		}
		else
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to place this sign");
			event.getBlock().breakNaturally();
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		try
		{
			if (plugin.getArenaManager().getGameManager().getPlayerStatus(event.getPlayer()).equals(InGameStatus.LOCKED))
				if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
					event.setCancelled(true);
		}
		catch (NullPointerException e)
		{
			return;
		}
	}
}
