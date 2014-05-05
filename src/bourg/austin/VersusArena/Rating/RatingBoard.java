package bourg.austin.VersusArena.Rating;

public class RatingBoard 
{
	private int gameType, ratingNum;
	
	public RatingBoard(int gameType, int ratingNum)
	{
		this.gameType = gameType;
		this.ratingNum = ratingNum;
	}
	
	public int getGameType()
	{
		return gameType;
	}
	
	public int getRatingNum()
	{
		return ratingNum;
	}
}
