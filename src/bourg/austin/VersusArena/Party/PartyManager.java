package bourg.austin.VersusArena.Party;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Tasks.PartyInviteCloseTask;

public class PartyManager
{	
	private VersusArena plugin;
	private HashMap<Integer, Party> parties;
	private HashMap<String, Integer> openInvites;
	private HashMap<String, PartyInviteCloseTask> closeInviteTasks;
	
	public PartyManager(VersusArena plugin)
	{
		this.plugin = plugin;
		parties = new HashMap<Integer, Party>();
		openInvites = new HashMap<String, Integer>();
		closeInviteTasks = new HashMap<String, PartyInviteCloseTask>();
		
		plugin.getServer().getPluginManager().registerEvents(new PartyListener(this), plugin);
	}
	
	public void createParty(String leader)
	{
		parties.put(Party.getCurrentID(), new Party(leader, this));
	}
	
	public ArrayList<String> getInvitees(int partyID)
	{
		ArrayList<String> marks = new ArrayList<String>();
		
		for (String playerName : openInvites.keySet())
			if (openInvites != null && openInvites.get(playerName) == partyID)
				marks.add(playerName);
		
		return marks;
	}
	
	public void deleteParty(int id)
	{
		ArrayList<String> marks = getInvitees(id);
		
		for (String playerName : openInvites.keySet())
			if (openInvites != null && openInvites.get(playerName) == id)
				marks.add(playerName);
		for (String playerName: marks)
		{
			closeInvite(playerName);
			if (plugin.getServer().getPlayer(playerName) != null)
				plugin.getServer().getPlayer(playerName).sendMessage(ChatColor.BLUE + "The party you were invited to has been disbanded.");
		}
	}
	
	public boolean isInvitedToParty(String playerName, int partyID)
	{
		if (openInvites.get(playerName) == null)
			return false;
		return openInvites.get(playerName) == partyID;
	}
	
	public void openInvite(String playerToInvite, int partyID)
	{
		if (openInvites.containsKey(playerToInvite))
			closeInvite(playerToInvite);
		openInvites.put(playerToInvite, partyID);
		
		closeInviteTasks.put(playerToInvite, new PartyInviteCloseTask(this, playerToInvite, parties.get(partyID)));
		closeInviteTasks.get(playerToInvite).runTaskLater(plugin, 1200);
	}
	
	public void closeInvite(String playerName)
	{
		openInvites.remove(playerName);
		closeInviteTasks.get(playerName).cancel();
		closeInviteTasks.remove(playerName);
	}
	
	public void acceptInvite(String invitedPlayer)
	{
		parties.get(openInvites.get(invitedPlayer)).addPlayer(invitedPlayer);
		closeInvite(invitedPlayer);
	}
	
	public boolean isInParty(String playerName)
	{
		for (Party party : parties.values())
			if (party.getMembers().contains(playerName))
				return true;
		return false;
	}
	
	public Party getParty(String playerName)
	{
		for (Party party : parties.values())
			if (party.getMembers().contains(playerName))
				return party;
		return null;
	}
	
	public Party getParty(int id)
	{
		return parties.get(id);
	}
	
	public boolean hasOpenInvite(String playerName)
	{
		return openInvites.keySet().contains(playerName);
	}
	
	public Party getInvitedParty(String playerName)
	{
		if (!hasOpenInvite(playerName)) return null;
		
		return parties.get(openInvites.get(playerName));
	}
	
	public VersusArena getPlugin()
	{
		return plugin;
	}
}
