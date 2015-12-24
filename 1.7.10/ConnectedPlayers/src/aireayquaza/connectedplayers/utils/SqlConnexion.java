package aireayquaza.connectedplayers.utils;

import java.sql.*;

import org.bukkit.ChatColor;

import aireayquaza.connectedplayers.ConnectedPlayers;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * The class who's job is to connect to a database ad talk with it.
 */
public class SqlConnexion
{
	private ConnectedPlayers plugin;
	private Connection sqlConnexion;
	
	/* ----- Constructors ----- */
	/**
	 * Create a SQL connexion
	 * @param plugin
	 * 		The plugin who need the connexion
	 * @param url
	 * 		The standard connexion url
	 * @param login
	 * 		The user login
	 * @param password
	 * 		The user password
	 */
	public SqlConnexion(ConnectedPlayers plugin, String url, String login, String password)
	{
		this.plugin = plugin;
		try
		{
			this.sqlConnexion = DriverManager.getConnection(url, login, password);
			this.createTable();
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] " + ChatColor.RED + "Unable to connect to database.");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe SQL server is down?");
			this.plugin.setPluginEnabled(false);
		}
	}
	
	/**
	 * Create a SQL connexion
	 * @param host
	 * 		The host
	 * @param port
	 * 		The host port
	 * @param dbname
	 * 		The database name
	 * @param login
	 * 		The user login
	 * @param password
	 * 		The user password
	 */
	public SqlConnexion(ConnectedPlayers plugin, String host, int port, String dbname, String login, String password)
	{
		this.plugin = plugin;
		try
		{
			this.sqlConnexion = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname, login, password);
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] " + ChatColor.RED + "Unable to connect to database.");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe SQL server is down?");
			this.plugin.setPluginEnabled(false);
		}
	}
	
	/* ----- Actions ----- */
	private void createTable()
	{
		try
		{
			Statement s = this.sqlConnexion.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS connectedplayers (player varchar(32) NOT NULL,PRIMARY KEY (player))");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] SQL server is " + ChatColor.GREEN + "[ON]");
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[ConnectedPlayers] Maybe SQL server is down?");
		}
	}
	
	/**
	 * Insert a player into the database by his name
	 * @param playerName
	 * 		The player name
	 * @throws SQLException
	 */
	public void insertPlayer(String playerName) throws SQLException
	{
		PreparedStatement s = this.sqlConnexion.prepareStatement("INSERT INTO connectedplayers(player) VALUES(?)");
		s.setString(1, playerName);
		s.executeUpdate();
	}
	
	/**
	 * Delete a player from the database by his name
	 * @param playerName
	 * 		The name of the player
	 * @throws SQLException
	 */
	public void deletePlayer(String playerName) throws SQLException
	{
		PreparedStatement s = this.sqlConnexion.prepareStatement("DELETE FROM connectedplayers WHERE player= ?");
		s.setString(1, playerName);
		s.executeUpdate();
	}
	
	/**
	 * Delete all players in the database
	 * @throws SQLException
	 */
	public void deleteAllPlayers() throws SQLException
	{
		this.sqlConnexion.createStatement().executeUpdate("DELETE FROM connectedplayers");
	}
	
	/**
	 * Close the connexion with database
	 */
	public void closeConnexion()
	{
		try
		{
			this.sqlConnexion.close();
		}
		catch (Exception e)
		{}
	}
}