package bourg.austin.VersusArena.Interface;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class DisplayBoard
{
	private Player player;
	
	private Scoreboard board;
	private Objective o;
	private HashMap<OfflinePlayer, Score> values;
	
	private ChatColor scoreColor, headerColor;
	
	public DisplayBoard(Player player, String title, ChatColor scoreColor, ChatColor headerColor)
	{
		this.player = player;
		this.scoreColor = scoreColor;
		this.headerColor = headerColor;
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		o = board.registerNewObjective(title, "dummy");
		o.setDisplayName(title);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		values = new HashMap<OfflinePlayer, Score>();
	}
	
	public void putField(String name, int value)
	{		
		String displayText = name + scoreColor + value;

		System.out.println("Putting \"" + name + value + "\"");
		
		System.out.println("Initial length is " + displayText.length() + " before alterations");
		
		while (displayText.length() < 16)
			displayText += " ";
		
		System.out.println("length is " + displayText.length() + " before alterations");
		
		
		while (true)
		{
			if (!values.keySet().contains(Bukkit.getOfflinePlayer(displayText)))
			{
				System.out.println("Break for good string");
				break;
			}
			
			displayText = displayText.substring(0, displayText.length() - 1);
			
			if (displayText.length() < (name + value).length())
			{
				System.out.println("Return for error");
				return;
			}
		}
		
		System.out.println("length is " + displayText.length() + " after alterations");
		
		values.put(Bukkit.getOfflinePlayer(displayText), o.getScore(Bukkit.getOfflinePlayer(displayText)));
		values.get(Bukkit.getOfflinePlayer(displayText)).setScore(0);
		for (OfflinePlayer p : values.keySet())
			values.get(p).setScore(values.get(p).getScore() + 1);
	}
	
	public void putHeader(String text)
	{		
		String displayText = headerColor + text;
		while (displayText.length() < 16)
			displayText += " ";
		values.put(Bukkit.getOfflinePlayer(displayText), o.getScore(Bukkit.getOfflinePlayer(displayText)));
		values.get(Bukkit.getOfflinePlayer(displayText)).setScore(0);
		for (OfflinePlayer p : values.keySet())
			values.get(p).setScore(values.get(p).getScore() + 1);
	}
	
	public void display()
	{
		player.setScoreboard(board);
	}
}