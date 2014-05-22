package bourg.austin.VersusArena.Party;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			player.sendMessage(ChatColor.RED + "You must be in the Versus Arena to use party commands.");
			return true;
		}
		
		//If the root command is /party
		if (cmd.getName().equalsIgnoreCase("party"))
		{
			//If there are args supplied
			if (args.length >= 1)
			{
				//If the first arg is 'create'
				if (args[0].equalsIgnoreCase("create") && sender.hasPermission("pairspvp.party"))
				{
					//If there is exactly one argument
					if (args.length == 1)
					{
						//If the player isn't already in a party
						if (!partyManager.isPlayerInParty(player.getName()))
						{
							partyManager.createParty(player.getName());
							player.sendMessage(ChatColor.BLUE + "Party created. Use " + ChatColor.WHITE + "/versus stats " + ChatColor.BLUE + "to show your main scoreboard.");
							partyManager.showPartyBoard(player);
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
						Party senderParty = partyManager.getParty(player.getName());
						if (senderParty != null && senderParty.getLeaderName().equalsIgnoreCase(player.getName()))
						{
							//If there are less than 3 party members
							if (senderParty.getMembers().size() < 3)
							{
								//If the sender is inviting somebody other than themself
								Player invitee = partyManager.getPlugin().getServer().getPlayer(args[1]);
								if (!player.getName().equalsIgnoreCase(invitee.getName()))
								{
									//If player being invited is online
									if (invitee != null && partyManager.getPlugin().getArenaManager().getOnlinePlayersInLobby().contains(invitee))
									{
										//If the player being invited isn't in a party already
										if (partyManager.isPlayerInParty(invitee.getName()))
										{
											partyManager.openInvite(invitee.getName(), senderParty.getID());
											player.sendMessage(ChatColor.BLUE + "Invite sent.");
											invitee.sendMessage(player.getName() + ChatColor.BLUE + " has sent you a party invite. This will expire one minute.");
											invitee.sendMessage(ChatColor.GOLD + "/party accept");
											invitee.sendMessage(ChatColor.GOLD + "/party deny");
										}
										//If the player being invited is in a party
										else
											player.sendMessage(ChatColor.RED + invitee.getName() + " is already in a party.");
									}
									//If the player being invited isn't online
									else
										player.sendMessage(args[1] + ChatColor.BLUE + " is not in the arena.");
								}
								//If the sender is inviting himself
								else
									sender.sendMessage(ChatColor.RED + "You can't invite yourself.");
							}
							//If there are already 3 party members
							else
								player.sendMessage(ChatColor.BLUE + "You can only have three people in your party.");
								
						}
						//If you aren't a party leader
						else
							player.sendMessage(ChatColor.RED + "You need to be a party leader to send invites.");
					}
					//If the arguments are wrong
					else
						sender.sendMessage(ChatColor.RED + "/party invite <playername>");
				}
			}
			
			return true;
		}
		return false;
	}
}
