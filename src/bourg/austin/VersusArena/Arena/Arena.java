package bourg.austin.VersusArena.Arena;

import org.bukkit.Location;

public class Arena
{
	private String arenaName;
	private int teamSize;
	private Location spawnLocations[][];
	private Location deathLocation;
	
	public Arena(String arenaName, int teamSize)
	{
		this.arenaName = arenaName;
		this.teamSize = teamSize;
		
		//First pos is team number, second pos is player number
		this.spawnLocations = new Location[2][teamSize];
		
	}
	
	public String getArenaName()
	{
		return arenaName;
	}
	
	public int getTeamSize()
	{
		return teamSize;
	}
	
	public boolean setSpawnLocation(int teamNumber, int playerNumber, Location loc)
	{
		//If team number or player number is invalid
		if (!(Math.abs(teamNumber) == 1 || Math.abs(teamNumber) == 0) || !(Math.abs(playerNumber) >= 0 && Math.abs(playerNumber) < teamSize))
		{
			return false;
		}
			
		spawnLocations[teamNumber][playerNumber] = loc;
		
		return true;
	}
	
	public boolean isConfigured()
	{
		if (deathLocation == null)
			return false;
		
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
	
	public void setDeathLocation(Location deathLocation)
	{
		this.deathLocation = deathLocation;
	}
	
	public Location getDeathLocation()
	{
		return deathLocation;
	}
}
