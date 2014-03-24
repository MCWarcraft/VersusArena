package bourg.austin.PairsPvP.Arena;

public class Competitor
{
	private int wins, losses, mmr, rating;
	private String name;
	
	public Competitor(String name)
	{
		this(name, 0, 0, 1500, 0);
	}
	
	public Competitor(String name, int wins, int losses, int mmr, int rating)
	{
		this.name = name;
		this.wins = wins;
		this.losses = losses;
		this.mmr = mmr;
		this.rating=rating;
	}
	
	public String getCompetitorName()
	{
		return name;
	}
	
	public int getWins()
	{
		return wins;
	}
	
	public int getLosses()
	{
		return losses;
	}
	
	public int getMMR()
	{
		return mmr;
	}
	
	public int getRating()
	{
		return rating;
	}
}
