package bourg.austin.PairsPvP.Interface;

import java.util.HashMap;

import org.bukkit.Bukkit;
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
	
	public DisplayBoard(Player player, String title)
	{
		this.player = player;
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		o = board.registerNewObjective(title, "dummy");
		o.setDisplayName(title);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		values = new HashMap<OfflinePlayer, Score>();
	}
	
	public void putField(String name, int value)
	{
		values.put(Bukkit.getOfflinePlayer(name), o.getScore(Bukkit.getOfflinePlayer(name)));
		values.get(Bukkit.getOfflinePlayer(name)).setScore(value);
	}
	
	public void display()
	{
		player.setScoreboard(board);
	}
}