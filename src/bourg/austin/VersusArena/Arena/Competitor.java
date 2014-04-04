package bourg.austin.VersusArena.Arena;

import java.util.HashMap;

import bourg.austin.VersusArena.Constants.VersusKits;

public class Competitor
{
	private int wins, losses, rating;
	private HashMap<String, Boolean> availableKits;
	private String selectedKitName;
	private String name;
	
	public Competitor(String name)
	{
		this(name, 0, 0, 0, "Default");
	}
	
	
	public Competitor(String name, int wins, int losses, int rating, String selectedKitName)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.rating = rating;
		
		//Kit permissions
		this.availableKits = new HashMap<String, Boolean>();
		for (String kitName : VersusKits.getKits().keySet())
			availableKits.put(kitName, false);
		availableKits.put("Default", true);
		
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
	
	public int getWins()
	{
		return wins;
	}
	
	public Competitor addWin()
	{
		wins++;
		return this;
	}
	
	public int getLosses()
	{
		return losses;
	}
	
	public Competitor addLoss()
	{
		losses++;
		return this;
	}
	
	public int getRating()
	{
		return rating;
	}
}
