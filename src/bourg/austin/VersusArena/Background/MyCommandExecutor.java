package bourg.austin.VersusArena.Background;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bourg.austin.VersusArena.VersusArena;

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
						//If a nexus exists
						if (plugin.getArenaManager().getNexusLocation() != null)
						{
							plugin.getArenaManager().bringPlayer(player.getName());
						}
						//If no nexus exists
						else
							sender.sendMessage(ChatColor.RED + "There is currently no PvP nexus. Please notify the mods.");
					}
				}
				//If the command user is the server
				else
					sender.sendMessage("Only players are able to go to the PvP nexus.");
			}
			//If some arguments have been provided
			else
			{
				//If user is trying to work with the nexus
				if (args[0].equalsIgnoreCase("nexus"))
				{
					//If the argument number is right and the argument is used
					if (args.length == 2 && args[1].equalsIgnoreCase("delete"))
					{						
						if (sender.hasPermission("pairspvp.nexus.delete"))
						{
							plugin.getArenaManager().setNexusLocation(null);
							player.sendMessage(ChatColor.GREEN + "The nexus has been removed.");
						}
					}
					
					//if user is trying to set the nexus
					else if (args.length == 2 && args[1].equalsIgnoreCase("set"))
					{
						//If the command sender is a player
						if (player != null)
						{
							if (player.hasPermission("pairspvp.nexus.set"))
							{
								//If the player has a location selected
								if (plugin.getSelectedLocation(sender.getName()) != null)
								{
									plugin.getArenaManager().setNexusLocation(plugin.getSelectedLocation(player.getName()));
									player.sendMessage(ChatColor.GREEN + "The nexus has been set at the selected location.");
								}
								//If the player does not have a location selected
								else
									sender.sendMessage(ChatColor.RED + "You must select a location for the nexus");
							}
						}
						
						//If the command sender is the console
						else
							sender.sendMessage("Only a player can set the nexus location.");
					}
					//if user is trying to do a nexus operation without 2 arguments
					else
						sender.sendMessage(ChatColor.RED + "Invalid command");
				}
				
				else if (args[0].equalsIgnoreCase("arena"))
				{
					//If there are enough arguments for something to be possible
					if (args.length != 1)
					{
						//If the user is trying to make a new arena
						if (args[1].equalsIgnoreCase("make"))
						{
							//If the player has permission
							if (sender.hasPermission("pairspvp.arena.make"))
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
												plugin.getArenaManager().addArena(args[2], teamSize);
												sender.sendMessage(ChatColor.GREEN + "The arena " + ChatColor.BLUE + args[2] + ChatColor.GREEN + " has been created.");
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
							//If the sender doesn't have pairspvp.arena.make
						}
						
						else if (args[1].equalsIgnoreCase("list"))
						{
							if (sender.hasPermission("pairspvp.arena.list"))
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
						}
						
						else if (args[1].equalsIgnoreCase("details"))
						{
							if (sender.hasPermission("pairspvp.arena.details"))
							{
								if (args.length == 3)
								{
									//If the arena exists
									if (plugin.getArenaManager().containsArena(args[2]))
									{
										Location spawns[][] = plugin.getArenaManager().getArena(args[2]).getSpawnLocations();
										Location tempLoc = spawns[0][0];
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
												tempLoc = spawns[team][playerNum];
												sender.sendMessage(ChatColor.AQUA + "Team " + (team + 1) + " Player " + (playerNum + 1) + ": " + (tempLoc == null ? ChatColor.RED + "Not set" : ChatColor.GREEN +   "(" + tempLoc.getBlockX() + ", " + tempLoc.getBlockY() + ", " + tempLoc.getBlockZ() + ") in world " + tempLoc.getWorld().getName()));
																							}
											}
										}
									
									//If the arena doesn't exist
									else
										sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " does not exist");
								}
								else
									sender.sendMessage(ChatColor.RED + "/versus arena details <arenaname>");
								
							}
							//If the command sender doesn't have permission auto return
						}
						
						else if (args[1].equalsIgnoreCase("delete"))
						{
							if (sender.hasPermission("pairspvp.arena.delete"))
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
						}
						//This is a copied block. Fix it.
						else if (args[1].equalsIgnoreCase("setspawn"))
						{
							//If the command sender is a player
							if (player != null)
							{
								if (player.hasPermission("pairspvp.arena.setspawn"))
								{
									//If the player has the correct permissions
									if (plugin.getArenaManager().containsArena(args[2]))
									{
										//If the correct number of arguments has been supplied
										if (args.length == 5)
										{
											int teamNum, playerNum;
											try
											{
												teamNum = Integer.parseInt(args[3]);
												playerNum = Integer.parseInt(args[4]);
											}
											catch (NumberFormatException e)
											{
												sender.sendMessage(ChatColor.RED + "The team number and spawn number must be valid integers.");
												return true;
											}
											//If the team number is valid
											if (teamNum == 1 || teamNum == 2)
											{
												//if the playerNum is valid
												if (playerNum >= 1 && playerNum <= plugin.getArenaManager().getArena(args[2]).getTeamSize())
												{
													//If the player has a location selected
													if (plugin.getSelectedLocation(sender.getName()) != null)
													{
														plugin.getArenaManager().getArena(args[2]).setSpawnLocation(teamNum - 1, playerNum - 1, plugin.getSelectedLocation(player.getName()));
														player.sendMessage(ChatColor.GREEN + "The spawn for " + ChatColor.BLUE + "Team " + args[3] + " Player " + args[4] + ChatColor.GREEN + " has been set at the selected location.");
													}
													//If the player does not have a location selected
													else
														sender.sendMessage(ChatColor.RED + "You must select a location for the spawn");
												}
												//If the playerNum isn't valid
												else
												{
													player.sendMessage(ChatColor.RED + "The player number must be between 1 and the team size");
												}
											}
											//If the team number is wrong
											else
												sender.sendMessage(ChatColor.RED + "The team number must be either 1 or 2");
										}
										//If the wrong number of arguments has been supplied
										else
											sender.sendMessage(ChatColor.RED + "/versus arena setspawn <arenaname> <teamnum> <playernum>");								
									}
									//If the arena doesn't exist
									else
										sender.sendMessage(ChatColor.RED + "The arena " + ChatColor.BLUE + args[2] + ChatColor.RED + " does not exist");
								}
								//If the player doesn't have permission nothing will happen
								
							}
							
							//If the command sender is the console
							else
								sender.sendMessage("Only a player can set a spawn location.");
						}
					}
					//If only the argument arena is provided
					else //TODO: Make this only go if the player has some relevant permission.
						sender.sendMessage(ChatColor.RED + "Invalid command");
				}
				
				//If the user is trying to use a config command
				else if (args[0].equalsIgnoreCase("config"))
				{
					if (args.length == 2 && args[1].equalsIgnoreCase("save"))
						if (sender.hasPermission("pairspvp.config.save"))
						{
							plugin.saveData();
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