package bourg.austin.VersusArena.Arena;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import bourg.austin.VersusArena.MatchmakingEntity;
import bourg.austin.VersusArena.Constants.GameResult;
import bourg.austin.VersusArena.Constants.GameType;
import bourg.austin.VersusArena.Constants.LobbyStatus;
import bourg.austin.VersusArena.Game.VersusTeam;
import core.Scoreboard.ScoreboardValue;

public class Competitor implements MatchmakingEntity, ScoreboardValue
{
	private Integer[] wins, losses, rating;
	private int kills, deaths;
	private UUID playerUUID;
	
	public Competitor(UUID playerUUID)
	{
		this(playerUUID, new Integer[]{0, 0, 0}, new Integer[]{0, 0, 0}, new Integer[]{1500, 1500, 1500}, 0, 0);
	}
	
	
	public Competitor(UUID playerUUID, Integer[] wins, Integer[] losses, Integer[] rating, int kills, int deaths)
	{
		this.playerUUID = playerUUID;
		this.wins = wins;
		this.losses = losses;
		this.rating = rating;
		this.kills = kills;
		this.deaths = deaths;
	}
	
	public UUID getCompetitorUUID()
	{
		return playerUUID;
	}
	
	public int getWins(GameType type)
	{
		return wins[type.getValue()];
	}
	
	public Competitor addWin(GameType type)
	{
		wins[type.getValue()]++;
		return this;
	}
	
	public int getLosses(GameType type)
	{
		return losses[type.getValue()];
	}
	
	public Competitor addLoss(GameType type)
	{
		losses[type.getValue()]++;
		return this;
	}
	
	public int getRating(GameType type)
	{
		return rating[type.getValue()];
	}
	
	public int getRating(LobbyStatus queueNum)
	{
		return rating[queueNum.getValue() - 1];
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public Competitor addKill()
	{
		kills++;
		return this;
	}
	
	public Competitor addDeath()
	{
		deaths++;
		return this;
	}
	
	public int updateRating(GameResult result, VersusTeam enemyTeam)
	{
		double expectedWinRate = 1.0 / (1.0 + Math.pow(10.0, (((double) enemyTeam.getPregameAverageRating() - rating[enemyTeam.getGame().getGameType().getValue()])) / 400.0));
		Double doubleDelta = new Double(30.0 * (((double) result.getValue()) - expectedWinRate));
		rating[enemyTeam.getGame().getGameType().getValue()] = rating[enemyTeam.getGame().getGameType().getValue()] + doubleDelta.intValue();
		
		return rating[enemyTeam.getGame().getGameType().getValue()];
	}
	
	public int getSize()
	{
		return 1;
	}
	
	public ArrayList<Competitor> getCompetitors()
	{
		ArrayList<Competitor> list = new ArrayList<Competitor>();
		list.add(this);
		
		return list;
	}
	
	public ArrayList<Player> getPlayers()
	{
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(Bukkit.getServer().getPlayer(playerUUID));
		
		return list;
	}
	
	@Override
	public String getScoreboardValue(String key)
	{
		switch (key)
		{
		case "wins1":
			return "" + getWins(GameType.ONE);
		case "losses1":
			return "" + getLosses(GameType.ONE);
		case "rating1":
			return "" + getRating(GameType.ONE);
		case "wins2":
			return "" + getWins(GameType.TWO);
		case "losses2":
			return "" + getLosses(GameType.TWO);
		case "rating2":
			return "" + getRating(GameType.TWO);
		case "wins3":
			return "" + getWins(GameType.THREE);
		case "losses3":
			return "" + getLosses(GameType.THREE);
		case "rating3":
			return "" + getRating(GameType.THREE);
		default:
			return "err3";
		}
	}
}
