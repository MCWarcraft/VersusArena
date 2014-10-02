package bourg.austin.VersusArena.Party;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import core.Utilities.UUIDCache;

public class PartyCommandExecutor implements CommandExecutor
{
	PartyManager partyManager;
	
	public PartyCommandExecutor(PartyManager partyManager)
	{
		this.partyManager = partyManager;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		
		if (player == null)
		{
			sender.sendMessage(ChatColor.RED + "Only a player can use party commands.");
			return true;
		}
		
		//If the player isn't in arena
		if (!partyManager.getPlugin().getArenaManager().getOnlinePlayersInLobby().contains(player))
		{
			player.sendMessage(ChatColor.RED + "You must be in the Arena to use party commands.");
			return true;
		}
		
		//If the root command is /party
		if (cmd.getName().equalsIgnoreCase("party") && sender.hasPermission("pairspvp.party"))
		{
			//If there are args supplied
			if (args.length >= 1)
			{
				//If the first arg is 'create'
				if (args[0].equalsIgnoreCase("create"))
				{
					//If there is exactly one argument
					if (args.length == 1)
					{
						//If the player isn't already in a party
						if (!partyManager.isInParty(player.getUniqueId()))
						{
							partyManager.createParty(player.getUniqueId());
							player.sendMessage(ChatColor.BLUE + "Party created. Use" + ChatColor.WHITE + " /party help " + ChatColor.BLUE + "for more commands.");
						}
						//If the player is already in a party
						else
							player.sendMessage(ChatColor.RED + "You are already in a party");
					}
					//If there are additional arguments supplied
					else
						sender.sendMessage(ChatColor.RED + "/party create");
				}
				
				//If the first arg is 'invite'
				else if (args[0].equalsIgnoreCase("invite"))
				{
					//If there are exactly two arguments
					if (args.length == 2)
					{
						//If you are a party leader
						Party senderParty = partyManager.getParty(player.getUniqueId());
						if (senderParty != null && senderParty.getLeaderUUID().equals(player.getUniqueId()))
						{
							//If there are less than 3 party members
							if (senderParty.getMemberUUIDs().size() < 3)
							{

								UUID inviteeUUID = UUIDCache.getPlayerUUID(args[1]);
								
								//If there is no cached UUID
								if (inviteeUUID == null)
								{
									player.sendMessage(ChatColor.RED + args[1] + " is not online.");
									return true;
								}
								
								//If the sender is inviting somebody other than themself
								if (!player.getUniqueId().equals(inviteeUUID))
								{									
									Player invitee = Bukkit.getPlayer(inviteeUUID);
									
									//If player being invited is not in the arena
									if (partyManager.getPlugin().getArenaManager().getOnlinePlayersInLobby().contains(invitee))
									{
										//If the player being invited doesn't already have an invite from you
										if (!partyManager.isInvitedToParty(invitee.getUniqueId(), senderParty.getID()))
										{
											//If the player being invited isn't in a party already
											if (!partyManager.isInParty(invitee.getUniqueId()))
											{
												partyManager.openInvite(invitee.getUniqueId(), senderParty.getID());
												player.sendMessage(ChatColor.BLUE + "Invite sent.");
												invitee.sendMessage(player.getName() + ChatColor.BLUE + " has sent you a party invite.");
												invitee.sendMessage(ChatColor.GOLD + "/party accept");
												invitee.sendMessage(ChatColor.GOLD + "/party deny");
												invitee.sendMessage(ChatColor.BLUE +  "This will expire one minute.");
											}
											//If the player being invited is in a party
											else
												player.sendMessage(ChatColor.RED + invitee.getName() + " is already in a party.");
										}
										else
											player.sendMessage(ChatColor.RED + "This player already has an open invite to your party.");
									}
									//If the player being invited isn't online
									else
										player.sendMessage(args[1] + ChatColor.RED + " is not in the arena.");
								}
								//If the sender is inviting himself
								else
									sender.sendMessage(ChatColor.RED + "You can't invite yourself.");
							}
							//If there are already 3 party members
							else
								player.sendMessage(ChatColor.RED + "You can only have three people in your party.");
								
						}
						//If you aren't a party leader
						else
							player.sendMessage(ChatColor.RED + "You need to be a party leader to send invites.");
					}
					//If the arguments are wrong
					else
						sender.sendMessage(ChatColor.RED + "/party invite <playername>");
				}
				//If the player is trying to accept an invite
				else if (args[0].equalsIgnoreCase("accept"))
				{
					//If exactly one argument has been supplied
					if (args.length == 1)
					{
						//If you have an open invite
						Party invitedParty = partyManager.getInvitedParty(player.getUniqueId());
						if (invitedParty != null)
						{
							//If there's room in the party
							if (invitedParty.getMemberUUIDs().size() < 3)
							{
								//If the player isn't in another party (should always work, just to be safe)
								if (!partyManager.isInParty(player.getUniqueId()))
								{
									player.sendMessage(ChatColor.BLUE + "You have joined a party.");
									invitedParty.broadcast(player.getName() + ChatColor.BLUE + " has joined your party.");
									partyManager.acceptInvite(player.getUniqueId());
								}
								else
								{
									sender.sendMessage(ChatColor.RED + "You are already in a party.");
									partyManager.closeInvite(player.getUniqueId());
								}
							}
							//If there's no room left
							else
							{
								player.sendMessage(ChatColor.RED + "There's no room left in the party.");
								partyManager.closeInvite(player.getUniqueId());
							}	
						}
						//If you have no open invite
						else
							player.sendMessage(ChatColor.RED + "You have no open invites.");
					}
					//If too many args have been supplied
					else
						player.sendMessage(ChatColor.RED + "/party accept");
				}
				//If the player is trying to deny an invite
				else if (args[0].equalsIgnoreCase("deny"))
				{
					//If exactly one argument has been supplied
					if (args.length == 1)
					{
						//If you have an open invite
						Party invitedParty = partyManager.getInvitedParty(player.getUniqueId());
						if (invitedParty != null)
						{
							player.sendMessage(ChatColor.BLUE + "You have denied the invite.");
							invitedParty.messageLeader(player.getName() + ChatColor.DARK_RED + " has denied your invite.");
							partyManager.closeInvite(player.getUniqueId());
						}
						//If you have no open invite
						else
							player.sendMessage(ChatColor.RED + "You have no open invites.");
					}
					//If too many args have been supplied
					else
						player.sendMessage(ChatColor.RED + "/party deny");
				}
				//If the player is trying to leave a party
				else if (args[0].equalsIgnoreCase("leave"))
				{
					//If exactly one arg is supplied
					if (args.length == 1)
					{
						//If the player is in a party
						if (partyManager.isInParty(player.getUniqueId()))
						{
							partyManager.getParty(player.getUniqueId()).playerLeave(player.getUniqueId(), true);
							player.sendMessage(ChatColor.BLUE + "You have left the party.");
							
						}
						//If the player isn't in a party
						else
							player.sendMessage(ChatColor.RED + "You aren't in a party.");
					}
					//If too many args have been supplied
					else
						player.sendMessage(ChatColor.RED + "/party leave");
				}
				//If the player is trying to kick
				else if (args[0].equalsIgnoreCase("kick"))
				{
					//If there are 2 arguments
					if (args.length == 2)
					{
						//If you are a party leader
						Party senderParty = partyManager.getParty(player.getUniqueId());
						if (senderParty != null && senderParty.getLeaderUUID().equals(player.getUniqueId()))
						{
							//If the player being kicked is offline
							UUID kickeeUUID = UUIDCache.getPlayerUUID(args[1]);
							if (kickeeUUID == null)
							{
								player.sendMessage(ChatColor.RED + args[1] + " is not online.");
							}
							
							Player kickee = Bukkit.getPlayer(kickeeUUID);
							
							if (kickee != null)
							{								
								//If the sender is inviting somebody other than themself
								if (!player.getUniqueId().equals(kickee.getUniqueId()))
								{
									//If the player being kicked is in the party
									if (senderParty.getMemberUUIDs().contains(kickee.getUniqueId()))
									{
										senderParty.playerLeave(kickee.getUniqueId(), false);
										kickee.sendMessage(ChatColor.DARK_RED + "You have been kicked from the party.");
										senderParty.broadcast(kickee.getName() + ChatColor.BLUE + " has been kicked from the party.");
									}
									//If the player being kicked isn't in the party
									else
										player.sendMessage(kickee.getName() + ChatColor.RED + " is not in your party.");
								}
								//If the sender is inviting himself
								else
									sender.sendMessage(ChatColor.RED + "You can't kick yourself.");
							}
							//If the player being kicked is offline
							else
								player.sendMessage(args[1] + ChatColor.RED + " is not in your party.");								
						}
						//If you aren't the party leader
						else
							player.sendMessage(ChatColor.RED + "You need to be a party leader to kick.");
					}
					//If the number of args is incorrect
					else
						player.sendMessage(ChatColor.RED + "/party kick <player>");
				}
				//fIf the player is trying to list party members
				else if (args[0].equalsIgnoreCase("list"))
				{
					//If exactly 1 argument is supplied
					if (args.length == 1)
					{
						//If player is in a party
						if (partyManager.isInParty(player.getUniqueId()))
						{
							Party party = partyManager.getParty(player.getUniqueId());
							
							player.sendMessage(ChatColor.GOLD + "-----Your Party-----");
							
							player.sendMessage(ChatColor.GOLD + "Leader: " + ChatColor.WHITE + Bukkit.getPlayer(party.getLeaderUUID()).getName());
							for (UUID pUUID : party.getMemberUUIDs())
								if (!pUUID.equals(party.getLeaderUUID()))
									player.sendMessage(ChatColor.YELLOW + "Member: " + ChatColor.WHITE + Bukkit.getPlayer(pUUID).getName());
							for (UUID pUUID : partyManager.getInvitees(party.getID()))
								player.sendMessage(ChatColor.DARK_AQUA + "INVITEE: " + ChatColor.WHITE + Bukkit.getPlayer(pUUID).getName());

							player.sendMessage(ChatColor.GOLD + "--------------------");
						}
						//If the player isn't in a party
						else
							player.sendMessage(ChatColor.RED + "You must be in a party to list party members.");
					}
					//If there are other arguments supplied
					else
						player.sendMessage(ChatColor.RED + "/party list");
				}
				//If the player is using the help command
				else if (args[0].equalsIgnoreCase("help"))
				{
					//If exactly 1 arg is supplied
					if (args.length == 1)
					{
						player.sendMessage(ChatColor.GOLD + "---PARTY COMMANDS---");
						player.sendMessage(ChatColor.DARK_AQUA + "/party create" + ChatColor.WHITE + ": Create a party.");
						player.sendMessage(ChatColor.DARK_AQUA + "/party invite <player>" + ChatColor.WHITE + ": Invite <player> to the party.");
						player.sendMessage(ChatColor.DARK_AQUA + "/party kick <player>" + ChatColor.WHITE + ": Kick <player> from the party.");
						player.sendMessage(ChatColor.DARK_AQUA + "/party leave" + ChatColor.WHITE + ": Leave the party.");
						player.sendMessage(ChatColor.DARK_AQUA + "/party list" + ChatColor.WHITE + ": List the player in your party.");
						player.sendMessage(ChatColor.DARK_AQUA + "/pc <message>" + ChatColor.WHITE + ": Send <message> to your party.");
						player.sendMessage(ChatColor.GOLD + "--------------------");
					}
					//If there are other arguments
					else
						player.sendMessage(ChatColor.RED + "/party help");						
				}
			}
			
			return true;
		}
		//If the command is party chat
		if (cmd.getName().equals("pc") && sender.hasPermission("pairspvp.party"))
		{
			Party party = partyManager.getParty(player.getUniqueId());
			//If the player is in a party
			if (party != null)
			{
				//If args are supplied
				if (args.length > 0)
				{					
					String fullMessage = "";
					for (String s : args)
						fullMessage = fullMessage + s + " ";
					
					party.broadcast(fullMessage, player.getUniqueId());
				}
				//If no args are supplied
				else
					player.sendMessage(ChatColor.RED + "/pc <message>");
			}
			//If the player isn't in a party
			else
				player.sendMessage(ChatColor.RED + "You need to be in a party to use party chat.");
			
			return true;
		}
		return false;
	}
}
