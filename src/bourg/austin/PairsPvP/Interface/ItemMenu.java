package bourg.austin.PairsPvP.Interface;

import org.bukkit.inventory.ItemStack;

import bourg.austin.PairsPvP.PairsPvP;

public class ItemMenu
{
	private String name;
	private int size;
	private PairsPvP plugin;
	
	private String[] optionNames;
	private ItemStack[] optionIcons;
	
	public ItemMenu(String name, int size, PairsPvP plugin)
	{
		this.name = name;
		this.size = size;
		this.plugin = plugin;
		
		optionNames = new String[size];
		optionIcons = new ItemStack[size];
	}
}
