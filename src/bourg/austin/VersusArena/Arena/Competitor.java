package bourg.austin.VersusArena.Arena;

import java.util.HashMap;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameResult;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.VersusTeam;

public class Competitor
{
	private Integer[] wins, losses, rating;
	private HashMap<String, Boolean> availableKits;
	private String selectedKitName;
	private String name;
	
	public Competitor(String name, VersusArena plugin)
	{
		this(name, new Integer[]{0, 0, 0}, new Integer[]{0, 0, 0}, new Integer[]{1500, 1500, 1500}, "Def", plugin);
	}
	
	
	public Competitor(String name, Integer[] wins, Integer[] losses, Integer[] rating, String selectedKitName, VersusArena plugin)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.rating = rating;
		
		//Kit permissions
		this.availableKits = new HashMap<String, Boolean>();
		for (String kitName : plugin.getVersusKits().getKits().keySet())
			availableKits.put(kitName, false);
		availableKits.put("Def", true);
		
		this.selectedKitName = selectedKitName;
	}
	
	public String getSelectedKitName()
	{
		return selectedKitName;
	}
	
	public void setAvailableKits(HashMap<String, Boolean> kits)
	{
		this.availableKits = kits;
	}
	
	public void setSelectedKitName(String selectedKitName)
	{
		this.selectedKitName = selectedKitName;
	}
	
	public HashMap<String, Boolean> getAvailableKits()
	{
		return availableKits;
	}
	
	public String getCompetitorName()
	{
		return name;
	}
	
	public int getWins(GameType type)
	{
		return wins[type.getValue()];
	}
	
	public Competitor addWin(GameType type)
	{
		wins[type.getValue()]++;
		return this;
	}
	
	public int getLosses(GameType type)
	{
		return losses[type.getValue()];
	}
	
	public Competitor addLoss(GameType type)
	{
		losses[type.getValue()]++;
		return this;
	}
	
	public int getRating(GameType type)
	{
		return rating[type.getValue()];
	}
	
	public int getRating(LobbyStatus queueNum)
	{
		return rating[queueNum.getValue() - 1];
	}
	
	public int updateRating(GameResult result, VersusTeam enemyTeam)
	{
		double expectedWinRate = 1.0 / (1.0 + Math.pow(10.0, (((double) enemyTeam.getPregameAverageRating() - rating[enemyTeam.getGame().getGameType().getValue()])) / 400.0));
		Double doubleDelta = new Double(30.0 * (((double) result.getValue()) - expectedWinRate));
		rating[enemyTeam.getGame().getGameType().getValue()] = rating[enemyTeam.getGame().getGameType().getValue()] + doubleDelta.intValue();
		
		return rating[enemyTeam.getGame().getGameType().getValue()];
	}
}
