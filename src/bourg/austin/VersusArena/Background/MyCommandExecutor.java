package bourg.austin.VersusArena.Background;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Arena.Arena;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import core.Utilities.LocationSelector;

public class MyCommandExecutor implements CommandExecutor
{
	private VersusArena plugin;
	
	public MyCommandExecutor(VersusArena plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player;
		if (sender instanceof Player)
			player = (Player) sender;
		else
			player = null;
		
		if (cmd.getName().equalsIgnoreCase("versus"))
		{
			//root command to go to nexus
			if (args.length == 0)
			{
				//If the command user is a player
				if (player != null)
				{
					if (player.hasPermission("pairspvp.go"))
					{
						player = (Player) sender;
						if (plugin.getArenaManager().getPlayerStatus(player.getUniqueId()) != LobbyStatus.IN_GAME)
						{
							//If a nexus exists
							if (plugin.getArenaManager().getNexusLocation() != null)
							{
								plugin.getArenaManager().bringPlayer(player.getUniqueId(), true);
							}
							//If no nexus exists
							else
								sender.sendMessage(ChatColor.RED + "There is currently no PvP nexus. Please notify the mods.");
						}
						else
							player.sendMessage(ChatColor.RED + "You can't use /versus in game.");
					}
				}
				//If the command user is the server
				else
					sender.sendMessage("Only players are able to go to the PvP nexus.");
			}
			//If some arguments have been provided
			else if (args.length >= 2)
			{
				//If user is trying to work with the nexus
				if (args[0].equalsIgnoreCase("nexus"))
				{
					//If the argument number is right and the argument is used
					if (args[1].equalsIgnoreCase("delete") && sender.hasPermission("pairspvp.nexus.delete"))
					{						
						if (args.length == 2)
						{
							plugin.getArenaManager().setNexusLocation(null);
							sender.sendMessage(ChatColor.GREEN + "The nexus has been removed.");
						}
						else
							sender.sendMessage(ChatColor.RED + "/versus nexus delete takes no extra arguments");
					}
					
					//if user is trying to set the nexus
					else if (args[1].equalsIgnoreCase("set") && player.hasPermission("pairspvp.nexus.set"))
					{
						//If the command sender is a player
						if (sender instanceof Player)
						{
							if (args.length == 2)
							{
								//If the player has a location selected
								if (LocationSelector.getSelectedLocation(player.getUniqueId()) != null)
								{
									plugin.getArenaManager().setNexusLocation(LocationSelector.getSelectedLocation(player.getUniqueId()));
									player.sendMessage(ChatColor.GREEN + "The nexus has been set at the selected location.");
								}
								//If the player does not have a location selected
								else
									sender.sendMessage(ChatColor.RED + "You must select a location for the nexus");
							}
							else
								sender.sendMessage(ChatColor.RED + "/versus nexus set takes no extra arguments");
						}
						else
							sender.sendMessage("Only a player can set the nexus location.");
					}
					//if user is trying to do a nexus operation without 2 arguments
					else
						sender.sendMessage(ChatColor.RED + "Invalid command");
				}
				
				else if (args[0].equalsIgnoreCase("arena"))
				{					
					//If the user is trying to make a new arena
					if (args[1].equalsIgnoreCase("make") && sender.hasPermission("pairspvp.arena.make"))
					{
						//If the correct number of arguments has been supplied
						if (args.length == 4)
						{
							//If the arena doesn't already exist
							if (!plugin.getArenaManager().containsArena(args[2]))
							{
								//If the name is of appropriate length
								if (args[2].length() > 0 && args[2].length() <= 15)
								{
									int teamSize = 0;
									try
									{
										teamSize = Integer.parseInt(args[3]);
									}
									catch (NumberFormatException e)
									{
										sender.sendMessage(ChatColor.RED + "Team size must be a valid integer");
										return true;
									}
									if (teamSize > 0 && teamSize <= 3)
									{
										plugin.getArenaSetupManager().openSetup(player, new Arena(args[2], teamSize));
									}
									else
										sender.sendMessage(ChatColor.RED + "Team size must be between 1, 2, or 3");
								}
								//If the name is too long
								else
									sender.sendMessage(ChatColor.RED + "The name " + ChatColor.BLUE + args[2] + ChatColor.RED + " is too long.");
							}
							//If the arena already exists
							else
								sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " already exists.");
						}
						//If the wrong number of arguments has been supplied
						else
							sender.sendMessage(ChatColor.RED + "/versus arena make <arena_name> <team_size>");
					}
					else if (args[1].equalsIgnoreCase("addinstance") && sender.hasPermission("pairspvp.arena.addinstance"))
					{
						//If the correct number of arguments has been supplied
						if (args.length == 3)
						{
							//If the arena already exists
							if (plugin.getArenaManager().containsArena(args[2]))
							{								
								//If the player has a location selected
								if (LocationSelector.getSelectedLocation(player.getUniqueId()) != null)
								{
									plugin.getArenaManager().getArena(args[2]).addOrigin(LocationSelector.getSelectedLocation(player.getUniqueId()));
									sender.sendMessage(ChatColor.GREEN + "New origin added");
								}
								//If the player doesn't have a location selected
								else
									sender.sendMessage(ChatColor.RED + "You must select a location for the origin.");
							}
							//If the arena doesn't already exist
							else
								sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " doesn't exist.");
						}
						//If the wrong number of arguments has been supplied
						else
							sender.sendMessage(ChatColor.RED + "/versus arena addinstance <arena_name>");
					}
					
					else if (args[1].equalsIgnoreCase("delinstance") && sender.hasPermission("pairspvp.arena.delinstance"))
					{
						//If the correct number of arguments has been supplied
						if (args.length == 3)
						{
							//If the arena already exists
							if (plugin.getArenaManager().containsArena(args[2]))
							{				
								Arena a = plugin.getArenaManager().getArena(args[2]);
								String smallestKey = null;
								double smallestDistance = 15;
								
								//Find the nearest location
								for (String originKey : a.getAllOrigins().keySet())
								{
									if (a.getAllOrigins().get(originKey).distance(player.getLocation()) < smallestDistance)
									{
										smallestKey = originKey;
										smallestDistance = a.getAllOrigins().get(originKey).distance(player.getLocation());
									}
								}
								
								if (smallestKey != null)
								{
									a.removeOrigin(smallestKey);
									player.sendMessage(ChatColor.GREEN + "Origin Key Deleted: " + ChatColor.DARK_AQUA + smallestKey);
								}
								else
									player.sendMessage(ChatColor.RED + "The arena " + a.getArenaName() + " doesn't have any origins within 15 blocks of you.");
							}
							//If the arena doesn't already exist
							else
								sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " doesn't exist.");
						}
						//If the wrong number of arguments has been supplied
						else
							sender.sendMessage(ChatColor.RED + "/versus arena addinstance <arena_name>");
					}
					
					else if (args[1].equalsIgnoreCase("list") && sender.hasPermission("pairspvp.arena.list"))
					{
						if (args.length == 2)
						{
							if (!(plugin.getArenaManager().getAllArenas().keySet().size() == 0))
							{
								sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Current Arenas");
								sender.sendMessage("");
								for (String s : plugin.getArenaManager().getAllArenas().keySet())
								{
									sender.sendMessage((plugin.getArenaManager().getArena(s).isConfigured() ? ChatColor.GREEN : ChatColor.RED) + s + ChatColor.BLUE + " (" + plugin.getArenaManager().getArena(s).getTeamSize() + (plugin.getArenaManager().getArena(s).getTeamSize() == 1 ? " Player)" : " Players)"));
								}
							}
							
							else
								sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "No arenas have been made");
						}
						else if (args.length == 3)
						{
							int teamSize;
							
							try
							{
								teamSize = Integer.parseInt(args[2]);
							}
							catch (NumberFormatException e)
							{
								sender.sendMessage("Team size must be a valid integer");
								return true;
							}
							
							ArrayList<String> properArenas = new ArrayList<String>();
							
							if (teamSize > 0 && teamSize <= 3)
							{
								for (String arenaName : plugin.getArenaManager().getAllArenas().keySet())
								{
									if (plugin.getArenaManager().getArena(arenaName).getTeamSize() == teamSize)
										properArenas.add(arenaName);
								}
							
								if (!(properArenas.size() == 0))
								{
									sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "Current " + teamSize + "v" + teamSize + " Arenas");
									sender.sendMessage("");
									for (String s : properArenas)
									{
										sender.sendMessage((plugin.getArenaManager().getArena(s).isConfigured() ? ChatColor.GREEN : ChatColor.RED) + s);
									}
								}
								
								else
									sender.sendMessage(ChatColor.BLUE + "" + ChatColor.UNDERLINE + "No " + teamSize + "v" + teamSize + " arenas have been made");
							}
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "/versus arena list <team_size_optional>");
						}
					}
					
					else if (args[1].equalsIgnoreCase("details") && sender.hasPermission("pairspvp.arena.details"))
					{
						if (args.length == 3)
						{
							//If the arena exists
							if (plugin.getArenaManager().containsArena(args[2]))
							{
								Vector spawns[][] = plugin.getArenaManager().getArena(args[2]).getRelativeSpawnLocations();
								Vector tempVec = spawns[0][0];
								//Header
								sender.sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Details on " + ChatColor.BLUE + "" + ChatColor.UNDERLINE + args[2]);
								//Configured
								sender.sendMessage("");
								sender.sendMessage((!plugin.getArenaManager().getArena(args[2]).isConfigured() ? ChatColor.RED + "Not Fully Configured" : ChatColor.GREEN + "Fully Configured"));
								//Coords
								for (int team = 0; team < 2; team++)
								{
									for (int playerNum = 0; playerNum < plugin.getArenaManager().getArena(args[2]).getTeamSize(); playerNum++)
									{
										tempVec = spawns[team][playerNum];
										sender.sendMessage(ChatColor.AQUA + "Team " + (team + 1) + " Player " + (playerNum + 1) + ": " + (tempVec == null ? ChatColor.RED + "Not set" : ChatColor.GREEN +   "(" + tempVec.getBlockX() + ", " + tempVec.getBlockY() + ", " + tempVec.getBlockZ() + ")"));															
									}
								}
								tempVec = plugin.getArenaManager().getArena(args[2]).getRelativeDeathLocation();
								sender.sendMessage(ChatColor.DARK_AQUA + "Death Spawn: " + (tempVec == null ? ChatColor.RED + "Not set" : ChatColor.GREEN +   "(" + tempVec.getBlockX() + ", " + tempVec.getBlockY() + ", " + tempVec.getBlockZ() + ")"));															
								
							}
						
							//If the arena doesn't exist
							else
								sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " does not exist");
						}
						else
							sender.sendMessage(ChatColor.RED + "/versus arena details <arenaname>");
					}
					
					else if (args[1].equalsIgnoreCase("delete") && sender.hasPermission("pairspvp.arena.delete"))
					{
						if (args.length == 3)
						{
							//If the arena exists
							if (plugin.getArenaManager().containsArena(args[2]))
							{
								plugin.getArenaManager().deleteArena(args[2]);
								sender.sendMessage(ChatColor.GREEN + "The arena " + ChatColor.BLUE + args[2] + ChatColor.GREEN + " has been removed.");
							}
							else
								sender.sendMessage(ChatColor.RED + "There is no arena called " + ChatColor.BLUE + args[2]);
						}
						//If the wrong number of args is supplied
						else
							sender.sendMessage(ChatColor.RED + "/versus arena delete <arena_name>");
					}
					//If only the argument arena is provided
					else
						sender.sendMessage(ChatColor.RED + "Invalid command");
				}
				
				//If the user is trying to use a config command
				else if (args[0].equalsIgnoreCase("config"))
				{
					if (args.length == 2 && args[1].equalsIgnoreCase("save"))
						if (sender.hasPermission("pairspvp.config.save"))
						{
							plugin.saveDatabase();
							sender.sendMessage(ChatColor.GREEN + "Data saved");
						}
					if (args.length == 2 && args[1].equalsIgnoreCase("load"))
						if (sender.hasPermission("pairspvp.config.load"))
						{
							plugin.loadData();
							sender.sendMessage(ChatColor.GREEN + "Data loaded");
						}
					
				}
				else
					sender.sendMessage(ChatColor.RED + args[0] + " is not a valid command subject.");
			}
			return true;
		}
		
		//Default return case
		return false;
	}
}