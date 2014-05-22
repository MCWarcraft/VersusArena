package bourg.austin.VersusArena.Party;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Interface.DisplayBoard;
import bourg.austin.VersusArena.Tasks.PartyInviteCloseTask;

public class PartyManager
{	
	private VersusArena plugin;
	private ArrayList<Party> parties;
	private HashMap<String, Integer> openInvites;
	private HashMap<String, PartyInviteCloseTask> closeInviteTasks;
	private HashMap<Player, DisplayBoard> boards;
	
	public PartyManager(VersusArena plugin)
	{
		this.plugin = plugin;
		parties = new ArrayList<Party>();
		openInvites = new HashMap<String, Integer>();
		closeInviteTasks = new HashMap<String, PartyInviteCloseTask>();
		boards = new HashMap<Player, DisplayBoard>();
	}
	
	public void createParty(String leader)
	{
		parties.add(new Party(leader));
	}
	
	public void openInvite(String playerToInvite, int partyID)
	{
		openInvites.put(playerToInvite, partyID);
		
		closeInviteTasks.put(playerToInvite, new PartyInviteCloseTask(this, playerToInvite));
		closeInviteTasks.get(playerToInvite).runTaskLater(plugin, 1200);
	}
	
	public void closeInvite(String playerName)
	{
		openInvites.remove(playerName);
		closeInviteTasks.remove(playerName);
	}
	
	public boolean isPlayerInParty(String playerName)
	{
		for (Party party : parties)
			if (party.getMembers().contains(playerName))
				return true;
		return false;
	}
	
	public Party getParty(String playerName)
	{
		for (Party party : parties)
			if (party.getMembers().contains(playerName))
				return party;
		return null;
	}
	
	public void showPartyBoard(Player player)
	{
		Party playerParty = getParty(player.getName());
		
		if (playerParty == null) return;
		
		boards.put(player, new DisplayBoard(player, ChatColor.BLUE + "Party", ChatColor.GREEN, ChatColor.YELLOW));
		
		boards.get(player).putHeader("[Leader]");
		boards.get(player).put(player.getName());
		
		boards.get(player).display();
	}
	
	public VersusArena getPlugin()
	{
		return plugin;
	}
}
