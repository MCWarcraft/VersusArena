package bourg.austin.VersusArena.Constants;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Inventories
{
	public static ItemStack[] LOBBY_SLOTS;
	public static ItemStack[] QUEUE_SLOTS;
	
	public static void initialize()
	{
		LOBBY_SLOTS = new ItemStack[3];
		ItemMeta tempMeta;
		
		LOBBY_SLOTS[0] = new ItemStack(Material.CLAY_BALL, 1);
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "1v1 Arena");
		tempMeta.setLore(Arrays.asList("Join the 1v1 queue"));
		LOBBY_SLOTS[0].setItemMeta(tempMeta);
		
		LOBBY_SLOTS[1] = new ItemStack(Material.CLAY_BALL, 1);
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "2v2 Arena");
		tempMeta.setLore(Arrays.asList("Join the 2v2 queue"));
		LOBBY_SLOTS[1].setItemMeta(tempMeta);
		
		LOBBY_SLOTS[2] = new ItemStack(Material.CLAY_BALL, 1);
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "3v3 Arena");
		tempMeta.setLore(Arrays.asList("Join the 3v3 queue"));
		LOBBY_SLOTS[2].setItemMeta(tempMeta);
		
		//TODO: Fix dye coloring
		QUEUE_SLOTS = new ItemStack[4];
		
		QUEUE_SLOTS[0] = new ItemStack(Material.SLIME_BALL, 1);
		tempMeta = QUEUE_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "1v1 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 1v1 queue"));
		QUEUE_SLOTS[0].setItemMeta(tempMeta);
		
		QUEUE_SLOTS[1] = new ItemStack(Material.SLIME_BALL, 1);
		tempMeta = QUEUE_SLOTS[1].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "2v2 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 2v2 queue"));
		QUEUE_SLOTS[1].setItemMeta(tempMeta);
		
		QUEUE_SLOTS[2] = new ItemStack(Material.SLIME_BALL, 1);
		tempMeta = QUEUE_SLOTS[2].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "3v3 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 3v3 queue"));
		QUEUE_SLOTS[2].setItemMeta(tempMeta);
		
		QUEUE_SLOTS[3] = new ItemStack(Material.FLINT, 1);
		tempMeta = QUEUE_SLOTS[3].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Exit queue");
		tempMeta.setLore(Arrays.asList("Remove yourself from the matchmaking queue"));
		QUEUE_SLOTS[3].setItemMeta(tempMeta);
		
	}
}
