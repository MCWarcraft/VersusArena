package bourg.austin.VersusArena.Arena;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Arena
{
	private String arenaName;
	private int teamSize;
	private HashMap<String, Location> origins;
	private HashMap<String, Boolean> availableOrigins;
	private Vector relativeSpawnLocations[][], spawnFacings[][];
	private Vector relativeDeathLocation, deathFacing;
	
	public Arena(String arenaName, int teamSize)
	{
		this.arenaName = arenaName;
		this.teamSize = teamSize;
		
		//First pos is team number, second pos is player number
		this.relativeSpawnLocations = new Vector[2][teamSize];
		this.spawnFacings = new Vector[2][teamSize];
		origins = new HashMap<String, Location>();
		availableOrigins = new HashMap<String, Boolean>();
	}
	
	public String getArenaName()
	{
		return arenaName;
	}
	
	public int getTeamSize()
	{
		return teamSize;
	}
	
	public boolean setRelativeSpawnLocation(int teamNumber, int playerNumber, Vector vector, Vector facing)
	{
		//If team number or player number is invalid
		if (!(Math.abs(teamNumber) == 1 || Math.abs(teamNumber) == 0) || !(Math.abs(playerNumber) >= 0 && Math.abs(playerNumber) < teamSize))
		{
			return false;
		}
		
		relativeSpawnLocations[teamNumber][playerNumber] = vector;
		spawnFacings[teamNumber][playerNumber] = facing;
		
		return true;
	}
	
	public boolean isConfigured()
	{
		if (relativeDeathLocation == null)
			return false;
		
		for (Vector[] ls : relativeSpawnLocations)
			for (Vector l : ls)
				if (l == null)
					return false;
	
		if (origins.size() == 0)
			return false;
		
		return true;
	}
	
	public Location[][] getSpawnLocations(String uuid)
	{
		Location[][] actualSpawnLocations = new Location[2][teamSize];

		for (int teamNum = 0; teamNum < relativeSpawnLocations.length; teamNum++)
			for (int playerNum = 0; playerNum < relativeSpawnLocations[teamNum].length; playerNum++)
				actualSpawnLocations[teamNum][playerNum] = origins.get(uuid).clone().add(relativeSpawnLocations[teamNum][playerNum]).setDirection(spawnFacings[teamNum][playerNum]);
		
		return actualSpawnLocations;
	}
	
	public Vector[][] getRelativeSpawnLocations()
	{
		return relativeSpawnLocations;
	}
	
	public void setRelativeDeathLocation(Vector deathLocation, Vector facing)
	{
		this.relativeDeathLocation = deathLocation;
		this.deathFacing = facing;
	}
	
	public Vector[][] getSpawnFacings()
	{
		return spawnFacings;
	}
	
	public Vector getDeathFacing()
	{
		return deathFacing;
	}
	
	public Vector getRelativeDeathLocation()
	{
		return relativeDeathLocation;
	}
	
	public Location getDeathLocation(String uuid)
	{
		return origins.get(uuid).clone().add(relativeDeathLocation).setDirection(deathFacing);
	}
	
	public void addOrigin(Location origin)
	{
		String uuid = UUID.randomUUID().toString();
		
		origins.put(uuid, origin);
		availableOrigins.put(uuid, true);
	}
	
	public void addOrigin(Location origin, String uuid)
	{
		origins.put(uuid, origin);
		availableOrigins.put(uuid, true);
	}
	
	public void removeOrigin(String uuid)
	{
		origins.remove(uuid);
		availableOrigins.remove(uuid);
	}
	
	public String checkoutInstance()
	{
		for (String uuid : availableOrigins.keySet())
			if (availableOrigins.get(uuid) == true)
			{
				availableOrigins.put(uuid, false);
				return uuid;
			}
		return null;
	}
	
	public void turnInInstance(String uuid)
	{
		availableOrigins.put(uuid, true);
	}
	
	public HashMap<String, Location> getAllOrigins()
	{
		return origins;
	}
}