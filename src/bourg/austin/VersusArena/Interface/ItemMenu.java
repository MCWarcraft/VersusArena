package bourg.austin.VersusArena.Interface;

import org.bukkit.inventory.ItemStack;

import bourg.austin.VersusArena.VersusArena;

public class ItemMenu
{
	private String name;
	private int size;
	private VersusArena plugin;
	
	private String[] optionNames;
	private ItemStack[] optionIcons;
	
	public ItemMenu(String name, int size, VersusArena plugin)
	{
		this.name = name;
		this.size = size;
		this.plugin = plugin;
		
		optionNames = new String[size];
		optionIcons = new ItemStack[size];
	}
}
