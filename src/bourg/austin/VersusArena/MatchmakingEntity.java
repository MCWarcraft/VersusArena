package bourg.austin.VersusArena;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import bourg.austin.VersusArena.Arena.Competitor;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.LobbyStatus;

public interface MatchmakingEntity
{
	public int getSize();
	
	public int getRating(GameType type);
	
	public int getRating(LobbyStatus status);
	
	public ArrayList<Competitor> getCompetitors();
	
	public ArrayList<Player> getPlayers();
}
