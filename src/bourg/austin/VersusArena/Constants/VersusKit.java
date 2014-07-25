package bourg.austin.VersusArena.Constants;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import core.Utilities.CoreUtilities;

public class VersusKit
{
	private static ItemStack[] contents;
	private static ItemStack[] armor;
	private static PotionEffect[] effects;
	
	public static void initialize(ItemStack[] contents, ItemStack[] armor, PotionEffect[] effects)
	{
		VersusKit.contents = contents;
		VersusKit.armor = armor;
		VersusKit.effects = effects;
	}
	
	@SuppressWarnings(value = { "deprecation" })
	public static void equipToPlayer(Player p)
	{
		CoreUtilities.resetPlayerState(p, false);
		
		for (ItemStack stack : contents)
			p.getInventory().addItem(stack);
		p.getInventory().setArmorContents(armor);
		for (PotionEffect effect: effects)
			p.addPotionEffect(effect);
		p.updateInventory();
	}
}
