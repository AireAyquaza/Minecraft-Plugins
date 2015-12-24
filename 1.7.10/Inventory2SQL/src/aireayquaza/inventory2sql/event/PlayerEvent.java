package aireayquaza.inventory2sql.event;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.json.JSONException;

import aireayquaza.inventory2sql.Inventory2SQL;
import aireayquaza.inventory2sql.JsonSerializer;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * All necessary events for saving/loading player's inventories
 */
public class PlayerEvent implements Listener
{
	private Inventory2SQL plugin;
	
	public PlayerEvent(Inventory2SQL plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) throws SQLException
	{
		String name = event.getPlayer().getName();
		ResultSet rs = this.plugin.getConnection().selectInventory(name);
		
		if (rs.next() != false)
		{
			try
			{
				this.plugin.getConnection().loadInventory(event.getPlayer(), rs);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			event.getPlayer().setLevel(Integer.parseInt(rs.getString("level")));
			event.getPlayer().setExp(Float.parseFloat(rs.getString("exp")));
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) throws SQLException
	{
		String playerName = event.getPlayer().getName();
		
		String inventory = JsonSerializer.serializeInventory(event.getPlayer().getInventory().getContents());
		String armor = JsonSerializer.serializeInventory(event.getPlayer().getInventory().getArmorContents());
		String enderchest = JsonSerializer.serializeInventory(event.getPlayer().getEnderChest().getContents());
		
		int level = event.getPlayer().getLevel();
		float exp = event.getPlayer().getExp();
		
		this.plugin.getConnection().saveInventory(playerName, inventory, armor, enderchest, level, exp);
	}
	
	@EventHandler
	public void onServerStop(ServerCommandEvent event) throws SQLException
	{
		String cmd = event.getCommand();
		if (cmd.equalsIgnoreCase("stop"))
		{
			for (World world : this.plugin.getServer().getWorlds())
			{
				for (Player player : world.getPlayers())
				{
					String playerName = player.getName();
					String inventory = JsonSerializer.serializeInventory(player.getInventory().getContents());
					String armor = JsonSerializer.serializeInventory(player.getInventory().getArmorContents());
					String enderchest = JsonSerializer.serializeInventory(player.getEnderChest().getContents());
					int level = player.getLevel();
					float exp = player.getExp();
					
					this.plugin.getConnection().saveInventory(playerName, inventory, armor, enderchest, level, exp);
				}
			}
		}
	}
	
	@EventHandler
	public void onServerStopByPlayer(PlayerCommandPreprocessEvent event) throws SQLException
	{
		String cmd = event.getMessage();
		if (cmd.equalsIgnoreCase("/stop"))
		{
			for (World world : this.plugin.getServer().getWorlds())
			{
				for (Player player : world.getPlayers())
				{
					String playerName = player.getName();
					String inventory = JsonSerializer.serializeInventory(player.getInventory().getContents());
					String armor = JsonSerializer.serializeInventory(player.getInventory().getArmorContents());
					String enderchest = JsonSerializer.serializeInventory(player.getEnderChest().getContents());
					int level = player.getLevel();
					float exp = player.getExp();
					
					this.plugin.getConnection().saveInventory(playerName, inventory, armor, enderchest, level, exp);
				}
			}
		}
	}
}
