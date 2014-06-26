package austin.CavemanSQL;

public enum PrimaryType
{
	NOT_SET(0), INT(1), STRING(2);
	
	private int value;
	
	private PrimaryType(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
}
