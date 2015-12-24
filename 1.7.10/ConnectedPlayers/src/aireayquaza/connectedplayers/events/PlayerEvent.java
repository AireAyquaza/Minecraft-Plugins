package aireayquaza.connectedplayers.events;

import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import aireayquaza.connectedplayers.ConnectedPlayers;

public class PlayerEvent implements Listener
{
	private ConnectedPlayers plugin;
	
	public PlayerEvent(ConnectedPlayers plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) throws SQLException
	{
		this.plugin.getConnection().insertPlayer(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) throws SQLException
	{
		this.plugin.getConnection().deletePlayer(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onServerStop(ServerCommandEvent event) throws SQLException
	{
		if (event.getCommand().equalsIgnoreCase("stop"))
			this.plugin.getConnection().deleteAllPlayers();
	}
	
	@EventHandler
	public void onServerStopByPlayer(PlayerCommandPreprocessEvent event) throws SQLException
	{
		if (event.getMessage().equalsIgnoreCase("/stop"))
			this.plugin.getConnection().deleteAllPlayers();
	}
}