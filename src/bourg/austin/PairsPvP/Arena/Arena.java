package bourg.austin.PairsPvP.Arena;

import org.bukkit.Location;

public class Arena
{
	private String arenaName;
	private Location spawnLocations[][];
	
	public Arena(String arenaName)
	{
		this.arenaName = arenaName;
		
		//First pos is team number, second pos is player number
		this.spawnLocations = new Location[2][2];
		
	}
	
	public String getArenaName()
	{
		return arenaName;
	}
	
	public boolean setSpawnLocation(int teamNumber, int playerNumber, Location loc)
	{
		//If team number or player number is invalid
		if (!(Math.abs(teamNumber) == 1 || Math.abs(teamNumber) == 0) || !(Math.abs(playerNumber) == 1 || Math.abs(playerNumber) == 0))
		{
			return false;
		}
			
		spawnLocations[teamNumber][playerNumber] = loc;
		
		return true;
	}
	
	public boolean isConfigured()
	{
		for (Location[] ls : spawnLocations)
			for (Location l : ls)
				if (l == null)
					return false;
	
		return true;
	}
	
	public Location[][] getSpawnLocations()
	{
		return spawnLocations;
	}
		
}
