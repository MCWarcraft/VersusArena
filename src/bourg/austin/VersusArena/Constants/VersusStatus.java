package bourg.austin.VersusArena.Constants;

public enum VersusStatus
{
	IN_LOBBY(0), IN_1V1_QUEUE(1), IN_2V2_QUEUE(2), IN_3V3_QUEUE(3), LOCKED(4), IN_GAME(5), ALIVE(6), DEAD(7);
	
	private int value;
	
	private VersusStatus(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
