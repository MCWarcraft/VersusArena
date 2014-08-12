package bourg.austin.VersusArena.Background;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Arena.Arena;
import core.Custody.CustodySwitchEvent;
import core.Utilities.LocationSelector;

public class ArenaSetupManager implements CommandExecutor, Listener
{
	private VersusArena plugin;
	private HashMap<String, Integer> setupStep;
	private HashMap<String, Arena> arenas;
	private HashMap<String, Location> originLocations;
	
	public ArenaSetupManager(VersusArena plugin)
	{
		this.plugin = plugin;
		setupStep = new HashMap<String, Integer>();
		arenas = new HashMap<String, Arena>();
		originLocations = new HashMap<String, Location>();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("vsetup").setExecutor(this);
		plugin.getCommand("vquit").setExecutor(this);
	}
	
	public void openSetup(Player player, Arena arena)
	{
		arenas.put(player.getName(), arena);
		setupStep.put(player.getName(), 0);

		this.sendInstructions(player, ChatColor.BLUE + "Use a stick to select the reference point.");
	}
	
	public void sendInstructions(Player player, String stepInstruction)
	{
		player.sendMessage("--------------------------------");
		player.sendMessage(stepInstruction);
		player.sendMessage(ChatColor.GOLD + "Use " + ChatColor.GREEN + "/vsetup " + ChatColor.GOLD + "to confirm your selection.");
		player.sendMessage(ChatColor.GOLD + "Use " + ChatColor.DARK_RED + "/vquit " + ChatColor.GOLD + "to stop setup.");
		player.sendMessage("--------------------------------");
	}
	
	private void finalizeArena(Player player)
	{
		plugin.getArenaManager().addArena(arenas.get(player.getName()));
		
		player.sendMessage(ChatColor.GREEN + "The arena " + arenas.get(player.getName()).getArenaName() + " now exists");
		
		arenas.remove(player.getName());
		setupStep.remove(player.getName());
		originLocations.remove(player.getName());
	}
	
	@EventHandler
	public void onCustodySwitch(CustodySwitchEvent event)
	{
		setupStep.remove(event.getPlayer().getName());
		arenas.remove(event.getPlayer().getName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Only players can use arena setup commands");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!setupStep.containsKey(player.getName()))
			return true;
		
		if (cmd.getName().equalsIgnoreCase("vsetup"))
		{
			if (setupStep.get(player.getName()) == 0)
			{
				if (LocationSelector.getSelectedLocation(player.getName()) != null)
				{
					originLocations.put(player.getName(), LocationSelector.getSelectedLocation(player.getName()));
					arenas.get(player.getName()).addOrigin(originLocations.get(player.getName()));
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the death location.");
					setupStep.put(player.getName(), 1);
				}
				else
					sendInstructions(player, ChatColor.RED + "You need to select a reference point.");
			}
			else if (setupStep.get(player.getName()) == 1)
			{
				Vector v = LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector();
				
				arenas.get(player.getName()).setRelativeDeathLocation(v, LocationSelector.getSelectedLocation(player.getName()).getDirection());
				sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 1");
				setupStep.put(player.getName(), 2);
			}
			else if (setupStep.get(player.getName()) == 2)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(0, 0, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				//If the arena is 1v1
				if (arenas.get(player.getName()).getTeamSize() == 1)
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
					setupStep.put(player.getName(), 5);
				}
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 2");
					setupStep.put(player.getName(), 3);
				}
			}
			else if (setupStep.get(player.getName()) == 3)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(0, 1, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				//If the arena is 2v2
				if (arenas.get(player.getName()).getTeamSize() == 2)
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
					setupStep.put(player.getName(), 5);
				}
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 3");
					setupStep.put(player.getName(), 4);
				}
			}
			else if (setupStep.get(player.getName()) == 4)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(0, 2, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
				setupStep.put(player.getName(), 5);
			}
			else if (setupStep.get(player.getName()) == 5)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(1, 0, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				//If the arena is 1v1
				if (arenas.get(player.getName()).getTeamSize() == 1)
					finalizeArena(player);
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 2");
					setupStep.put(player.getName(), 6);
				}
			}
			else if (setupStep.get(player.getName()) == 6)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(1, 1, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				//If the arena is 1v1
				if (arenas.get(player.getName()).getTeamSize() == 2)
					finalizeArena(player);
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 3");
					setupStep.put(player.getName(), 7);
				}
			}
			else if (setupStep.get(player.getName()) == 7)
			{
				arenas.get(player.getName()).setRelativeSpawnLocation(1, 2, LocationSelector.getSelectedLocation(player.getName()).clone().subtract(originLocations.get(player.getName())).toVector(), LocationSelector.getSelectedLocation(player.getName()).getDirection());
				//If the arena is 1v1
				finalizeArena(player);
			}
			
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("vquit"))
		{
			setupStep.remove(player.getName());
			arenas.remove(player.getName());
			originLocations.remove(player.getName());
			player.sendMessage(ChatColor.RED + "Setup aborted");
			return true;
		}
		
		return false;
	}
}