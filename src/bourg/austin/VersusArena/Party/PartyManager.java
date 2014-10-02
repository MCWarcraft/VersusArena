package bourg.austin.VersusArena.Party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Tasks.PartyInviteCloseTask;

public class PartyManager
{	
	private VersusArena plugin;
	private HashMap<Integer, Party> parties;
	private HashMap<UUID, Integer> openInvites;
	private HashMap<UUID, PartyInviteCloseTask> closeInviteTasks;
	
	public PartyManager(VersusArena plugin)
	{
		this.plugin = plugin;
		parties = new HashMap<Integer, Party>();
		openInvites = new HashMap<UUID, Integer>();
		closeInviteTasks = new HashMap<UUID, PartyInviteCloseTask>();
		
		plugin.getServer().getPluginManager().registerEvents(new PartyListener(this), plugin);
	}
	
	public void createParty(UUID leaderUUID)
	{
		parties.put(Party.getCurrentID(), new Party(leaderUUID, this));
	}
	
	public ArrayList<UUID> getInvitees(int partyID)
	{
		ArrayList<UUID> marks = new ArrayList<UUID>();
		
		for (UUID playerUUID : openInvites.keySet())
			if (openInvites != null && openInvites.get(playerUUID) == partyID)
				marks.add(playerUUID);
		
		return marks;
	}
	
	public void deleteParty(int id)
	{
		ArrayList<UUID> marks = getInvitees(id);
		
		for (UUID playerUUID : openInvites.keySet())
			if (openInvites != null && openInvites.get(playerUUID) == id)
				marks.add(playerUUID);
		for (UUID playerUUID : marks)
		{
			closeInvite(playerUUID);
			if (plugin.getServer().getPlayer(playerUUID) != null)
				plugin.getServer().getPlayer(playerUUID).sendMessage(ChatColor.BLUE + "The party you were invited to has been disbanded.");
		}
	}
	
	public boolean isInvitedToParty(UUID playerUUID, int partyID)
	{
		if (openInvites.get(playerUUID) == null)
			return false;
		return openInvites.get(playerUUID) == partyID;
	}
	
	public void openInvite(UUID playerUUIDToInvite, int partyID)
	{
		if (openInvites.containsKey(playerUUIDToInvite))
			closeInvite(playerUUIDToInvite);
		openInvites.put(playerUUIDToInvite, partyID);
		
		closeInviteTasks.put(playerUUIDToInvite, new PartyInviteCloseTask(this, playerUUIDToInvite, parties.get(partyID)));
		closeInviteTasks.get(playerUUIDToInvite).runTaskLater(plugin, 1200);
	}
	
	public void closeInvite(UUID playerUUID)
	{
		openInvites.remove(playerUUID);
		closeInviteTasks.get(playerUUID).cancel();
		closeInviteTasks.remove(playerUUID);
	}
	
	public void acceptInvite(UUID invitedUUID)
	{
		parties.get(openInvites.get(invitedUUID)).addPlayer(invitedUUID);
		closeInvite(invitedUUID);
	}
	
	public boolean isInParty(UUID playerUUID)
	{
		for (Party party : parties.values())
			if (party.getMemberUUIDs().contains(playerUUID))
				return true;
		return false;
	}
	
	public Party getParty(UUID playerUUID)
	{
		for (Party party : parties.values())
			if (party.getMemberUUIDs().contains(playerUUID))
				return party;
		return null;
	}
	
	public Party getParty(int id)
	{
		return parties.get(id);
	}
	
	public boolean hasOpenInvite(UUID playerUUID)
	{
		return openInvites.keySet().contains(playerUUID);
	}
	
	public Party getInvitedParty(UUID playerUUID)
	{
		if (!hasOpenInvite(playerUUID)) return null;
		
		return parties.get(openInvites.get(playerUUID));
	}
	
	public VersusArena getPlugin()
	{
		return plugin;
	}
}
