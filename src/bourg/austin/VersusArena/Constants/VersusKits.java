package bourg.austin.VersusArena.Constants;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VersusKits
{
	private static HashMap<String, VersusKit> kits;
	
	public static void initialize()
	{
		kits = new HashMap<String, VersusKit>();
		kits.put("Default", new VersusKit(new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD, 1)}, new ItemStack[]{}));
	}
	
	public static HashMap<String, VersusKit> getKits()
	{
		return kits;
	}
}
