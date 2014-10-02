package bourg.austin.VersusArena.Background;

import java.util.HashMap;
import java.util.UUID;

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
	private HashMap<UUID, Integer> setupStep;
	private HashMap<UUID, Arena> arenas;
	private HashMap<UUID, Location> originLocations;
	
	public ArenaSetupManager(VersusArena plugin)
	{
		this.plugin = plugin;
		setupStep = new HashMap<UUID, Integer>();
		arenas = new HashMap<UUID, Arena>();
		originLocations = new HashMap<UUID, Location>();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getCommand("vsetup").setExecutor(this);
		plugin.getCommand("vquit").setExecutor(this);
	}
	
	public void openSetup(Player player, Arena arena)
	{
		arenas.put(player.getUniqueId(), arena);
		setupStep.put(player.getUniqueId(), 0);

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
		plugin.getArenaManager().addArena(arenas.get(player.getUniqueId()));
		
		player.sendMessage(ChatColor.GREEN + "The arena " + arenas.get(player.getUniqueId()).getArenaName() + " now exists");
		
		arenas.remove(player.getUniqueId());
		setupStep.remove(player.getUniqueId());
		originLocations.remove(player.getUniqueId());
	}
	
	@EventHandler
	public void onCustodySwitch(CustodySwitchEvent event)
	{
		setupStep.remove(event.getPlayer().getUniqueId());
		arenas.remove(event.getPlayer().getUniqueId());
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
		
		if (!setupStep.containsKey(player.getUniqueId()))
			return true;
		
		if (cmd.getName().equalsIgnoreCase("vsetup"))
		{
			if (setupStep.get(player.getUniqueId()) == 0)
			{
				if (LocationSelector.getSelectedLocation(player.getUniqueId()) != null)
				{
					originLocations.put(player.getUniqueId(), LocationSelector.getSelectedLocation(player.getUniqueId()));
					arenas.get(player.getUniqueId()).addOrigin(originLocations.get(player.getUniqueId()));
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the death location.");
					setupStep.put(player.getUniqueId(), 1);
				}
				else
					sendInstructions(player, ChatColor.RED + "You need to select a reference point.");
			}
			else if (setupStep.get(player.getUniqueId()) == 1)
			{
				Vector v = LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector();
				
				arenas.get(player.getUniqueId()).setRelativeDeathLocation(v, LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 1");
				setupStep.put(player.getUniqueId(), 2);
			}
			else if (setupStep.get(player.getUniqueId()) == 2)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(0, 0,
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				
				//If the arena is 1v1
				if (arenas.get(player.getUniqueId()).getTeamSize() == 1)
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
					setupStep.put(player.getUniqueId(), 5);
				}
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 2");
					setupStep.put(player.getUniqueId(), 3);
				}
			}
			else if (setupStep.get(player.getUniqueId()) == 3)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(0, 1, 
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				//If the arena is 2v2
				if (arenas.get(player.getUniqueId()).getTeamSize() == 2)
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
					setupStep.put(player.getUniqueId(), 5);
				}
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 1 player 3");
					setupStep.put(player.getUniqueId(), 4);
				}
			}
			else if (setupStep.get(player.getUniqueId()) == 4)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(0, 2,
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 1");
				setupStep.put(player.getUniqueId(), 5);
			}
			else if (setupStep.get(player.getUniqueId()) == 5)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(1, 0,
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				//If the arena is 1v1
				if (arenas.get(player.getUniqueId()).getTeamSize() == 1)
					finalizeArena(player);
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 2");
					setupStep.put(player.getUniqueId(), 6);
				}
			}
			else if (setupStep.get(player.getUniqueId()) == 6)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(1, 1,
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				//If the arena is 1v1
				if (arenas.get(player.getUniqueId()).getTeamSize() == 2)
					finalizeArena(player);
				else
				{
					sendInstructions(player, ChatColor.BLUE + "Use a stick to select the spawn for team 2 player 3");
					setupStep.put(player.getUniqueId(), 7);
				}
			}
			else if (setupStep.get(player.getUniqueId()) == 7)
			{
				arenas.get(player.getUniqueId()).setRelativeSpawnLocation(1, 2,
						LocationSelector.getSelectedLocation(player.getUniqueId()).clone().subtract(originLocations.get(player.getUniqueId())).toVector(),
						LocationSelector.getSelectedLocation(player.getUniqueId()).getDirection());
				//If the arena is 1v1
				finalizeArena(player);
			}
			
			return true;
		}
		else if (cmd.getName().equalsIgnoreCase("vquit"))
		{
			setupStep.remove(player.getUniqueId());
			arenas.remove(player.getUniqueId());
			originLocations.remove(player.getUniqueId());
			player.sendMessage(ChatColor.RED + "Setup aborted");
			return true;
		}
		
		return false;
	}
}