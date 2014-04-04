package bourg.austin.VersusArena.Arena;

public class Competitor
{
	private int wins, losses, rating;
	private String name;
	
	public Competitor(String name)
	{
		this(name, 0, 0, 0);
	}
	
	public Competitor(String name, int wins, int losses, int rating)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.rating = rating;
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
