package bourg.austin.VersusArena.Constants;

public enum GameResult
{
	WIN(1), LOSS(0);
	
	private int value;
	
	private GameResult(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
