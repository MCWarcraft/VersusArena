package bourg.austin.VersusArena.Constants;

public enum InGameStatus
{
	LOCKED(10), ALIVE(11), DEAD(12);
	
	private int value;
	
	private InGameStatus(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
