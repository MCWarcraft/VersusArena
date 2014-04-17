package bourg.austin.VersusArena.Constants;

import java.util.HashMap;

public class VersusKits
{
	private HashMap<String, VersusKit> kits;
	
	public VersusKits()
	{
		kits = new HashMap<String, VersusKit>();
	}
	
	public HashMap<String, VersusKit> getKits()
	{
		return kits;
	}
	
	public boolean addKit(String name, VersusKit kit)
	{
		for (String s : kits.keySet())
			if (name.equalsIgnoreCase(s))
				return false;
		
		kits.put(name, kit);
		System.out.println("The kit " + name + " has been added to VersusKits");
		return true;
	}
}
