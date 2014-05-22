package bourg.austin.VersusArena.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import bourg.austin.VersusArena.Party.PartyManager;

public class PartyInviteCloseTask extends BukkitRunnable
{
	private PartyManager partyManager;
	private String invitedPlayer;
	
	public PartyInviteCloseTask(PartyManager partyManager, String invitedPlayer)
	{
		this.partyManager = partyManager;
		this.invitedPlayer = invitedPlayer;
	}
	
	@Override
	public void run()
	{
		partyManager.closeInvite(invitedPlayer);
	}
}