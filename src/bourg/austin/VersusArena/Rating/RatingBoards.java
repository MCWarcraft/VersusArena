package bourg.austin.VersusArena.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import bourg.austin.VersusArena.VersusArena;
import bourg.austin.VersusArena.Game.Task.RatingBoardUpdateDelayTask;

public class RatingBoards implements Listener
{
	private VersusArena plugin;
	
	public RatingBoards(VersusArena plugin)
	{
		this.plugin = plugin;
		
		//Configure main player data table
		PreparedStatement openSignDataStatement;
		try
		{
			plugin.openConnection();
			openSignDataStatement = plugin.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS rating_signs " +
					"( id int NOT NULL AUTO_INCREMENT," +
						"location varchar(255)," +
						"PRIMARY KEY (id) " +
					")");
			
			openSignDataStatement.execute();
			openSignDataStatement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			plugin.closeConnection();
		}
	}
	
	private RatingBoard getAsRatingBoard(String lineOne)
	{
		int ratingNumber; 
		try
		{
			if (lineOne.charAt(0) != '#')
				return null;
			ratingNumber = Integer.parseInt(lineOne.substring(1, lineOne.indexOf(" ")));
			if (ratingNumber < 1)
				return null;
			String type = lineOne.substring(lineOne.indexOf("-") + 2, lineOne.length());
			String number = "" + type.charAt(0) + type.charAt(2);
			if (!(number.equals("11") || number.equals("22") || number.equals("33")))
				return null;
			if (!(type.charAt(1) == 'v' || type.charAt(1) == 'V'))
				return null;
			
			return new RatingBoard(Integer.parseInt(number.substring(0, 1)), ratingNumber);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	@EventHandler
	public void addRatingBoard(SignChangeEvent event)
	{		
		if (!event.getPlayer().hasPermission("pairspvp.rating.make"))
			return;
		
		try
		{
			RatingBoard board = getAsRatingBoard(event.getLine(0));
			if (board == null)
				return;
			
			plugin.openConnection();
			PreparedStatement addNewSignStatement = plugin.getConnection().prepareStatement("INSERT INTO rating_signs SET location=?");
			addNewSignStatement.setString(1, VersusArena.locationToString(event.getBlock().getLocation()));
			addNewSignStatement.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			plugin.closeConnection();
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RatingBoardUpdateDelayTask(plugin), 1);
	}
	
	@EventHandler
	public void removeRatingBoard(BlockBreakEvent event)
	{
		if (!event.getPlayer().hasPermission("pairspvp.rating.break"))
		{
			event.setCancelled(true);
			return;
		}
		
		if (event.getBlock().getState() instanceof Sign)
		{
			Sign sign = (Sign) event.getBlock().getState();
			if (getAsRatingBoard(sign.getLine(0)) != null)
				removeRatingBoard(event.getBlock().getLocation());
		}
	}
	
	public void removeRatingBoard(Location loc)
	{
		try
		{
			plugin.openConnection();
			PreparedStatement deleteSignStatement = plugin.getConnection().prepareStatement("DELETE FROM rating_signs WHERE location = ?");
			deleteSignStatement.setString(1, VersusArena.locationToString(loc));
			deleteSignStatement.execute();							
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateBoards()
	{
		plugin.openConnection();
		try
		{
			PreparedStatement getAllSignsStatement = plugin.getConnection().prepareStatement("SELECT location FROM rating_signs");
			ResultSet allSignsResultSet = getAllSignsStatement.executeQuery(); 
			
			while (allSignsResultSet.next())
			{				
				Location loc = plugin.parseLocation(allSignsResultSet.getString("location"));

				if (!(loc.getBlock().getState() instanceof Sign))
					removeRatingBoard(loc);
				else
				{
					Sign tempSign = (Sign) loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getState();
					
					RatingBoard tempBoard = getAsRatingBoard(tempSign.getLine(0));
					if (tempBoard == null)
						removeRatingBoard(loc);
					else
					{
						PreparedStatement getPlayerStatement = plugin.getConnection().prepareStatement("SELECT player, rating" + tempBoard.getGameType() + " FROM player_data ORDER BY rating" + tempBoard.getGameType() + " DESC ,player ASC LIMIT " + (tempBoard.getRatingNum() - 1) + ",1");
						ResultSet playerOfRating = getPlayerStatement.executeQuery();
						if (playerOfRating.next())
						{
							tempSign.setLine(1, playerOfRating.getString("player"));
							tempSign.setLine(2, "" + playerOfRating.getInt("rating" + tempBoard.getGameType()));
							tempSign.setLine(3, "");
							tempSign.update();
						}
						else
						{
							tempSign.setLine(1, "");
							tempSign.setLine(2, "No player yet");
							tempSign.setLine(3, "");
							tempSign.update();
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			
		}
		
	}
}
