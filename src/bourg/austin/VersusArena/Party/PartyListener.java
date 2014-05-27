package bourg.austin.VersusArena.Party;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener
{
	private PartyManager partyManager;

	public PartyListener(PartyManager partyManager)
	{
		this.partyManager = partyManager;
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event)
	{
		Party party = partyManager.getParty(event.getPlayer().getName());
		if (party == null) return;
		party.playerLeave(event.getPlayer().getName(), true);
	}
}
