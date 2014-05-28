package bourg.austin.VersusArena.Constants;

public enum LobbyStatus
{
	IN_LOBBY(0), IN_1V1_QUEUE(1), IN_2V2_QUEUE(2), IN_3V3_QUEUE(3), IN_GAME(4), OFFLINE(5), IN_PARTY(6);
	
	private int value;
	
	private LobbyStatus(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
