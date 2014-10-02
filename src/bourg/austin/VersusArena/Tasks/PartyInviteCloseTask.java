package bourg.austin.VersusArena.Tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Party.Party;
import bourg.austin.VersusArena.Party.PartyManager;

public class PartyInviteCloseTask extends BukkitRunnable
{
	private PartyManager partyManager;
	private Party invitedParty;
	private UUID invitedPlayerUUID;
	
	public PartyInviteCloseTask(PartyManager partyManager, UUID invitedPlayerUUID, Party invitedParty)
	{
		this.partyManager = partyManager;
		this.invitedPlayerUUID = invitedPlayerUUID;
		this.invitedParty = invitedParty;
	}
	
	@Override
	public void run()
	{
		partyManager.getPlugin().getServer().getPlayer(invitedPlayerUUID).sendMessage(ChatColor.BLUE + "Your invite has expired.");
		invitedParty.broadcast(Bukkit.getServer().getPlayer(invitedPlayerUUID).getName() + ChatColor.BLUE + "'s invite has expired.");
		partyManager.closeInvite(invitedPlayerUUID);
	}
}