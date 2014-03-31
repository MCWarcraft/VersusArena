package bourg.austin.VersusArena.Constants;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class Inventories
{
	public static ItemStack[] LOBBY_SLOTS;
	public static ItemStack[] QUEUE_SLOTS;
	
	public static void initialize()
	{
		LOBBY_SLOTS = new ItemStack[3];
		ItemMeta tempMeta;
		
		Wool greenWool = new Wool(DyeColor.GREEN), grayWool = new Wool(DyeColor.GREEN);
		
		LOBBY_SLOTS[0] = new ItemStack(Material.WOOL, 1, (byte) 14);
		LOBBY_SLOTS[0].setData(new Wool(DyeColor.GRAY));
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "1v1 Arena");
		tempMeta.setLore(Arrays.asList("Join the 1v1 queue"));
		LOBBY_SLOTS[0].setItemMeta(tempMeta);
		
		LOBBY_SLOTS[1] = new ItemStack(Material.WOOL, 1, (byte) 14);
		LOBBY_SLOTS[1].setData(new Wool(DyeColor.GRAY));
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "2v2 Arena");
		tempMeta.setLore(Arrays.asList("Join the 2v2 queue"));
		LOBBY_SLOTS[1].setItemMeta(tempMeta);
		
		LOBBY_SLOTS[2] = new ItemStack(Material.WOOL, 1, (byte) 14);
		LOBBY_SLOTS[2].setData(new Wool(DyeColor.GRAY));
		tempMeta = LOBBY_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "3v3 Arena");
		tempMeta.setLore(Arrays.asList("Join the 3v3 queue"));
		LOBBY_SLOTS[2].setItemMeta(tempMeta);
		
		//TODO: Fix dye coloring
		QUEUE_SLOTS = new ItemStack[3];
		
		QUEUE_SLOTS[0] = new ItemStack(Material.WOOL, 1, (byte) 5);
		QUEUE_SLOTS[0].setData(new Wool(DyeColor.GREEN));
		tempMeta = QUEUE_SLOTS[0].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "1v1 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 1v1 queue"));
		QUEUE_SLOTS[0].setItemMeta(tempMeta);
		
		QUEUE_SLOTS[1] = new ItemStack(Material.WOOL, 1, (byte) 5);
		QUEUE_SLOTS[1].setData(new Wool(DyeColor.GREEN));
		tempMeta = QUEUE_SLOTS[1].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "2v2 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 2v2 queue"));
		QUEUE_SLOTS[1].setItemMeta(tempMeta);
		
		QUEUE_SLOTS[2] = new ItemStack(Material.WOOL, 1, (byte) 5);
		QUEUE_SLOTS[2].setData(new Wool(DyeColor.GREEN));
		tempMeta = QUEUE_SLOTS[2].getItemMeta();
		tempMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.GOLD + "" + ChatColor.BOLD + "3v3 Arena");
		tempMeta.setLore(Arrays.asList("You are in the 3v3 queue"));
		QUEUE_SLOTS[2].setItemMeta(tempMeta);
		
	}
}
