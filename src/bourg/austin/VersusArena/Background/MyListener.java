package bourg.austin.VersusArena.Background;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.InGameStatus;
import bourg.austin.VersusArena.Constants.Inventories;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.Game;
import core.Custody.CustodySwitchEvent;
import core.HonorPoints.OnlinePlayerCurrencyUpdateEvent;


public class MyListener implements Listener
{
	private static VersusArena plugin;

	public MyListener(VersusArena plugin)
	{
		MyListener.plugin = plugin;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event)
	{		
		Player p = event.getPlayer();

		//If the action was a right click on a sign with first line "/versus" and the player has permission		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && ((event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))) && ((Sign) event.getClickedBlock().getState()).getLine(0).equalsIgnoreCase(ChatColor.DARK_BLUE + "/versus") && p.hasPermission("pairspvp.arena.go"))
		{
			if (plugin.getArenaManager().getNexusLocation() != null)
			{
				plugin.getArenaManager().bringPlayer(p.getName(), true);
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
				else if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.PARTY_LOBBY.getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.PARTY_LOBBY.getItemMeta().getLore()))
					plugin.getArenaManager().addPartyToQueue(plugin.getPartyManager().getParty(event.getPlayer().getName()).getID());
				if (event.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals(Inventories.QUEUE_SLOTS[3].getItemMeta().getDisplayName()) && event.getPlayer().getItemInHand().getItemMeta().getLore().equals(Inventories.QUEUE_SLOTS[3].getItemMeta().getLore()))
				{
					plugin.getArenaManager().removeFromQueue(event.getPlayer());
					plugin.getArenaManager().removePartyFromQueue(plugin.getPartyManager().getParty(event.getPlayer().getName()).getID());
				}
			}
			catch (NullPointerException e)
			{

			}
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
			if (plugin.getArenaManager().getGameManager().getPlayerStatus(event.getPlayer()) == InGameStatus.LOCKED)
				if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY())
					event.setCancelled(true);
		}
		catch (NullPointerException e)
		{
			return;
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntity() == null)
			return;
		else if (!(event.getEntity() instanceof Player))
			return;

		Player damagedPlayer = (Player) event.getEntity();
		Damageable dp = (Damageable)damagedPlayer;
		
		if (plugin.getArenaManager().getPlayerStatus(damagedPlayer.getName()) == null)
			return;
		//If the player is in the arena system but not in game
		if (plugin.getArenaManager().getPlayerStatus(damagedPlayer.getName()) != LobbyStatus.IN_GAME)
		{
			event.setCancelled(true);
			return;
		}

		Game game = plugin.getArenaManager().getGameManager().getGameByParticipant(damagedPlayer);

		//Return if the player is not in a game
		if (game == null)
			return;

		//Cancel damage if player is dead
		if (game.getGameManager().getPlayerStatus(damagedPlayer) == InGameStatus.DEAD)
			event.setCancelled(true);

		if (dp.getHealth() - getDamageArmored(damagedPlayer, event.getDamage()) > 0)
			return;

		event.setCancelled(true);
		
		plugin.getCompetitorManager().updateCompetitor(plugin.getCompetitorManager().getCompetitor(damagedPlayer).addDeath());
		killPlayer(damagedPlayer, game);

	}

	//Used to handle deaths in arena
	@EventHandler
	public void onPlayerDamageByEntity(EntityDamageByEntityEvent event)
	{		
		if (event.getEntity() == null)
			return;
		else if (!(event.getEntity() instanceof Player))
			return;

		Player damagedPlayer = (Player) event.getEntity();
		Damageable dp = (Damageable)damagedPlayer;

		if (plugin.getArenaManager().getPlayerStatus(damagedPlayer.getName()) == null)
			return;
		//If the player is in the arena system but not in game
		if (plugin.getArenaManager().getPlayerStatus(damagedPlayer.getName()) != LobbyStatus.IN_GAME)
		{
			event.setCancelled(true);
			return;
		}

		Game game = plugin.getArenaManager().getGameManager().getGameByParticipant(damagedPlayer);

		if (game == null)
			return;

		Player damagingPlayer;
		try
		{
			damagingPlayer = (Player) event.getDamager();
		}
		catch (ClassCastException e)
		{
			return;
		}

		//If ghost attack
		if (game.getGameManager().getPlayerStatus(damagingPlayer) == InGameStatus.DEAD)
		{
			damagingPlayer.sendMessage(ChatColor.DARK_RED + "You are a ghost");
			event.setCancelled(true);
			return;
		}

		//If friendly fire
		if (game.areTeammates(damagingPlayer, damagedPlayer))
		{
			damagingPlayer.sendMessage(ChatColor.DARK_RED + "Don't hit teammates");
			event.setCancelled(true);
			return;
		}

		if (dp.getHealth() - getDamageArmored(damagedPlayer, event.getDamage()) > 0)
			return;

		//To get to this point a player in a game must have died
		event.setCancelled(true);

		plugin.getCompetitorManager().updateCompetitor(plugin.getCompetitorManager().getCompetitor(damagedPlayer).addDeath());
		plugin.getCompetitorManager().updateCompetitor(plugin.getCompetitorManager().getCompetitor(damagingPlayer).addKill());
		
		killPlayer(damagedPlayer, game);
	}

	private double getDamageArmored(Player player, double raw)
	{
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
		ItemStack boots = inv.getBoots();
		ItemStack helmet = inv.getHelmet();
		ItemStack chest = inv.getChestplate();
		ItemStack pants = inv.getLeggings();
		double red = 0.0;

		if (helmet != null)
		{
			if(helmet.getType() == Material.LEATHER_HELMET)
				red = red + 0.04;
			else if(helmet.getType() == Material.GOLD_HELMET)
				red = red + 0.08;
			else if(helmet.getType() == Material.CHAINMAIL_HELMET)
				red = red + 0.08;
			else if(helmet.getType() == Material.IRON_HELMET)
				red = red + 0.08;
			else if(helmet.getType() == Material.DIAMOND_HELMET)
				red = red + 0.12;
		}
		//
		if (boots != null)
		{
			if(boots.getType() == Material.LEATHER_BOOTS)
				red = red + 0.04;
			else if(boots.getType() == Material.GOLD_BOOTS)
				red = red + 0.04;
			else if(boots.getType() == Material.CHAINMAIL_BOOTS)
				red = red + 0.04;
			else if(boots.getType() == Material.IRON_BOOTS)
				red = red + 0.08;
			else if(boots.getType() == Material.DIAMOND_BOOTS)
				red = red + 0.12;
		}
		//
		if (pants != null)
		{
			if(pants.getType() == Material.LEATHER_LEGGINGS)
				red = red + 0.08;
			else if(pants.getType() == Material.GOLD_LEGGINGS)
				red = red + 0.12;
			else if(pants.getType() == Material.CHAINMAIL_LEGGINGS)
				red = red + 0.16;
			else if(pants.getType() == Material.IRON_LEGGINGS)
				red = red + 0.20;
			else if(pants.getType() == Material.DIAMOND_LEGGINGS)
				red = red + 0.24;
		}
		//
		if (chest != null)
		{
			if(chest != null && chest.getType() == Material.LEATHER_CHESTPLATE)
				red = red + 0.12;
			else if(chest.getType() == Material.GOLD_CHESTPLATE)
				red = red + 0.20;
			else if(chest.getType() == Material.CHAINMAIL_CHESTPLATE)
				red = red + 0.20;
			else if(chest.getType() == Material.IRON_CHESTPLATE)
				red = red + 0.24;
			else if(chest.getType() == Material.DIAMOND_CHESTPLATE)
				red = red + 0.32;
		}

		return raw * (1.0 - red);
	}

	private void killPlayer(Player damagedPlayer, Game game)
	{
		damagedPlayer.setHealth(20);
		
		//Hide the player from sight
		for (Player p : game.getPlayers())
			if (!damagedPlayer.equals(p))
				p.hidePlayer(damagedPlayer);

		plugin.getArenaManager().getGameManager().setPlayerStatus(damagedPlayer, InGameStatus.DEAD);
		
		game.broadcast(ChatColor.BLUE + damagedPlayer.getName() + " has fallen!");
		damagedPlayer.teleport(game.getArena().getDeathLocation(game.getArenaID()));
		game.checkGameOver();
	}
	
	@EventHandler
	public void onCustodySwitch(CustodySwitchEvent event)
	{
		executeExit(event.getPlayer());
	}

	@EventHandler
	public void onPlayerCurrencyUpdateEvent(OnlinePlayerCurrencyUpdateEvent event)
	{
		//If the player is in arena in some capacity and not in game
		if (plugin.getArenaManager().getPlayerStatus(event.getPlayer().getName()) != null && !(plugin.getArenaManager().getPlayerStatus(event.getPlayer().getName()) == LobbyStatus.IN_GAME))
		{
			plugin.getArenaManager().generateLobbyBoard(event.getPlayer());
		}
	}

	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.getArenaManager().cleanPlayer(event.getPlayer());
	}
	
	public static void executeExit(Player player)
	{
		plugin.getArenaManager().removePlayer(player);

		Game game = plugin.getArenaManager().getGameManager().getGameByParticipant(player);

		if (game == null)
			return;
		if (!game.getPlayers().contains(player))
			return;

		game.quit(player.getName());
		game.broadcast(ChatColor.DARK_RED + player.getName() + " has left the game.");
		
		game.getGameManager().setPlayerStatus(player, InGameStatus.DEAD);

		game.checkGameOver();
	}
}
