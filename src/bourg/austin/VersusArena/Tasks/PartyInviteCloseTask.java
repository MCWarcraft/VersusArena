package bourg.austin.VersusArena.Tasks;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Party.Party;
import bourg.austin.VersusArena.Party.PartyManager;

public class PartyInviteCloseTask extends BukkitRunnable
{
	private PartyManager partyManager;
	private Party invitedParty;
	private String invitedPlayer;
	
	public PartyInviteCloseTask(PartyManager partyManager, String invitedPlayer, Party invitedParty)
	{
		this.partyManager = partyManager;
		this.invitedPlayer = invitedPlayer;
		this.invitedParty = invitedParty;
	}
	
	@Override
	public void run()
	{
		partyManager.getPlugin().getServer().getPlayer(invitedPlayer).sendMessage(ChatColor.BLUE + "Your invite has expired.");
		invitedParty.broadcast(invitedPlayer +  ChatColor.BLUE + "'s invite has expired.");
		partyManager.closeInvite(invitedPlayer);
	}
}