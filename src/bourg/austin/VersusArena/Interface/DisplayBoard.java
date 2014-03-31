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
	
	private ChatColor titleColor, scoreColor;
	
	public DisplayBoard(Player player, String title, ChatColor titleColor, ChatColor scoreColor)
	{
		this.player = player;
		this.titleColor = titleColor;
		this.scoreColor = scoreColor;
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		o = board.registerNewObjective(title, "dummy");
		o.setDisplayName(title);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		values = new HashMap<OfflinePlayer, Score>();
	}
	
	public void putField(String name, int value)
	{		
		String displayText = titleColor + name + scoreColor + value;
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