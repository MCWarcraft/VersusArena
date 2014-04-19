package bourg.austin.VersusArena.Constants;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersusKit
{
	private ItemStack[] contents;
	private ItemStack[] armor;
	
	public VersusKit(ItemStack[] contents, ItemStack[] armor)
	{
		this.contents = contents;
		this.armor = armor;
	}
	
	@SuppressWarnings(value = { "deprecation" })
	public void equipToPlayer(Player p)
	{
		p.getInventory().clear();
		for (ItemStack stack : contents)
			p.getInventory().addItem(stack);
		p.getInventory().setArmorContents(armor);
		p.updateInventory();
	}
}
