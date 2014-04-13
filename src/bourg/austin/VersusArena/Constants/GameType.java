package bourg.austin.VersusArena.Constants;

public enum GameType
{
	ONE(0), TWO(1), THREE(2);
	
	private int value;
	
	private GameType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
