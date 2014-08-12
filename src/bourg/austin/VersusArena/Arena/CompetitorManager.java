package bourg.austin.VersusArena.Arena;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Constants.GameType;

public class CompetitorManager
{
	private VersusArena plugin;
	
	public CompetitorManager(VersusArena plugin)
	{
		this.plugin = plugin;
	}
	
	public Competitor getCompetitor(OfflinePlayer player)
	{
		Competitor tempCompetitor = new Competitor(player.getName());
		
		try
		{
			PreparedStatement getCompetitorStatement = plugin.getConnection().prepareStatement("SELECT * FROM player_data WHERE player=?");
			getCompetitorStatement.setString(1, player.getName());
			ResultSet competitorResult = getCompetitorStatement.executeQuery();
			
			if (competitorResult.next())
			{
				tempCompetitor = new Competitor(competitorResult.getString("player"),
						new Integer[] {
						competitorResult.getInt("wins1"),
						competitorResult.getInt("wins2"),
						competitorResult.getInt("wins3")},
						
						new Integer[] {
							competitorResult.getInt("losses1"),
							competitorResult.getInt("losses2"),
							competitorResult.getInt("losses3")},
						
						new Integer[] {
							competitorResult.getInt("rating1"),
							competitorResult.getInt("rating2"),
							competitorResult.getInt("rating3")},
							
						competitorResult.getInt("kills"),
						
						competitorResult.getInt("deaths")
						);
			}
			getCompetitorStatement.close();
			competitorResult.close();
			
			return tempCompetitor;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return tempCompetitor;
	}
	
	public void updateCompetitor(Competitor updatedCompetitor)
	{
		OfflinePlayer p = Bukkit.getOfflinePlayer(updatedCompetitor.getCompetitorName());

		PreparedStatement saveStatement = null;
		try
		{
			boolean playerDataExists;
			playerDataExists = plugin.isStringInCol("player_data", "player", p.getName());
			
			String query = (playerDataExists ? "UPDATE" : "INSERT INTO") + " player_data SET " +
					"wins1=?, losses1=?, rating1=?, wins2=?, losses2=?, rating2=?, wins3=?, losses3=?, rating3=?, kills=?, deaths=?" + (playerDataExists ? " WHERE player = '" + p.getName() + "'" : ", player=?");
							
			saveStatement = plugin.getConnection().prepareStatement(query);
			if (!playerDataExists)
				saveStatement.setString(12, p.getName());
			
			saveStatement.setInt(1, updatedCompetitor.getWins(GameType.ONE));
			saveStatement.setInt(4, updatedCompetitor.getWins(GameType.TWO));
			saveStatement.setInt(7, updatedCompetitor.getWins(GameType.THREE));
			
			saveStatement.setInt(2, updatedCompetitor.getLosses(GameType.ONE));
			saveStatement.setInt(5, updatedCompetitor.getLosses(GameType.TWO));
			saveStatement.setInt(8, updatedCompetitor.getLosses(GameType.THREE));
			
			saveStatement.setInt(3, updatedCompetitor.getRating(GameType.ONE));
			saveStatement.setInt(6, updatedCompetitor.getRating(GameType.TWO));
			saveStatement.setInt(9, updatedCompetitor.getRating(GameType.THREE));
			
			saveStatement.setInt(10, updatedCompetitor.getKills());
			saveStatement.setInt(11, updatedCompetitor.getDeaths());
			
			if (playerDataExists)
				saveStatement.executeUpdate();
			else
				saveStatement.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public VersusArena getPlugin()
	{
		return plugin;
	}
}