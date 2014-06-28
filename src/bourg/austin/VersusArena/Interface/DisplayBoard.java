package bourg.austin.VersusArena.Interface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import randy.core.ScoreboardValue;

public class DisplayBoard
{
	private Player player;
	
	private Scoreboard board;
	private Objective o;
	private HashMap<OfflinePlayer, Score> values;
	private ArrayList<String> titles, fixedValues;
	private ArrayList<ScoreboardValue> dynamicValues;
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
		titles = new ArrayList<String>();
		fixedValues = new ArrayList<String>();
		dynamicValues = new ArrayList<ScoreboardValue>();
	}
	
	public void putField(String title, String value)
	{
		titles.add(title);
		fixedValues.add(value);
		dynamicValues.add(null);
	}
	
	public void putField(String title, int value)
	{
		putField(title, "" + value);
	}
	
	public void putField(String title, ScoreboardValue value)
	{
		titles.add(title);
		fixedValues.add(null);
		dynamicValues.add(value);
	}
	
	public void putHeader(String text)
	{
		titles.add(text);
		fixedValues.add(null);
		dynamicValues.add(null);
	}
	
	public void putSpace()
	{
		titles.add(" ");
		fixedValues.add(null);
		dynamicValues.add(null);
	}
	
	public void update()
	{		
		ArrayList<String> finalStrings = padDuplicates(constructLines());
		values.clear();
		
		for (int i = 0; i < finalStrings.size(); i++)
		{
			values.put(Bukkit.getOfflinePlayer(finalStrings.get(i)), o.getScore(Bukkit.getOfflinePlayer(finalStrings.get(i))));
			values.get(Bukkit.getOfflinePlayer(finalStrings.get(i))).setScore(finalStrings.size() - i);
		}
		player.setScoreboard(board);
	}
	
	public void hide()
	{
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}
	
	private String constructLine(int line)
	{
		//If dynamic value
		if (fixedValues.get(line) == null && dynamicValues.get(line) != null)
			return titles.get(line) + scoreColor + dynamicValues.get(line).getScoreboardValue();
		//If static value
		else if (dynamicValues.get(line) == null && fixedValues.get(line) != null)
			return titles.get(line) + scoreColor + fixedValues.get(line);
		//If header
		else if (dynamicValues.get(line) == null && fixedValues.get(line) == null && titles.get(line) != null)
			return headerColor + titles.get(line);
		//Else if it's just a space
		else
			return " ";
	}
	
	private ArrayList<String> constructLines()
	{
		ArrayList<String> lines = new ArrayList<String>();
		for (int i = 0; i < titles.size(); i++)
			lines.add(constructLine(i));
		return lines;
	}
	
	private ArrayList<String> padDuplicates(ArrayList<String> original)
	{
		ArrayList<String> padded = new ArrayList<String>();
		
		for (int i = 0; i < original.size(); i++)
		{
			padded.add(original.get(i) + StringUtils.repeat(" ", Collections.frequency(original.subList(0, i), original.get(i))));
			if (padded.get(padded.size() - 1).length() > 16)
				padded.remove(padded.size() - 1);				
		}
		
		return padded;		
	}
}