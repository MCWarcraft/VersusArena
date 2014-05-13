package bourg.austin.VersusArena.Arena;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameResult;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.VersusTeam;

public class Competitor
{
	private Integer[] wins, losses, rating;
	private int kills, deaths;
	private String name;
	
	public Competitor(String name, VersusArena plugin)
	{
		this(name, new Integer[]{0, 0, 0}, new Integer[]{0, 0, 0}, new Integer[]{1500, 1500, 1500}, 0, 0, plugin);
	}
	
	
	public Competitor(String name, Integer[] wins, Integer[] losses, Integer[] rating, int kills, int deaths, VersusArena plugin)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.rating = rating;
		this.kills = kills;
		this.deaths = deaths;
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
	
	public int getKills()
	{
		return kills;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public Competitor addKill()
	{
		kills++;
		return this;
	}
	
	public Competitor addDeath()
	{
		deaths++;
		return this;
	}
	
	public int updateRating(GameResult result, VersusTeam enemyTeam)
	{
		double expectedWinRate = 1.0 / (1.0 + Math.pow(10.0, (((double) enemyTeam.getPregameAverageRating() - rating[enemyTeam.getGame().getGameType().getValue()])) / 400.0));
		Double doubleDelta = new Double(30.0 * (((double) result.getValue()) - expectedWinRate));
		rating[enemyTeam.getGame().getGameType().getValue()] = rating[enemyTeam.getGame().getGameType().getValue()] + doubleDelta.intValue();
		
		return rating[enemyTeam.getGame().getGameType().getValue()];
	}
}
