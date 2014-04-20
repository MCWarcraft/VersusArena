package bourg.austin.VersusArena.Constants;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersusKit
{
	private static ItemStack[] contents;
	private static ItemStack[] armor;
	
	public static void initialize(ItemStack[] contents, ItemStack[] armor)
	{
		VersusKit.contents = contents;
		VersusKit.armor = armor;
	}
	
	@SuppressWarnings(value = { "deprecation" })
	public static void equipToPlayer(Player p)
	{
		p.getInventory().clear();
		for (ItemStack stack : contents)
			p.getInventory().addItem(stack);
		p.getInventory().setArmorContents(armor);
		p.updateInventory();
	}
}
