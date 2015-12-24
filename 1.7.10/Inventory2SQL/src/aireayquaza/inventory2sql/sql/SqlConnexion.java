package aireayquaza.inventory2sql.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONException;

import aireayquaza.inventory2sql.Inventory2SQL;
import aireayquaza.inventory2sql.JsonDeserializer;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * The class who's job is to connect to a database ad talk with it.
 */
public class SqlConnexion
{
	private Inventory2SQL plugin;
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
	public SqlConnexion(Inventory2SQL plugin, String url, String login, String password)
	{
		this.plugin = plugin;
		try
		{
			this.sqlConnexion = DriverManager.getConnection(url, login, password);
			this.createTable();
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[Inventory2SQL] " + ChatColor.RED + "Unable to connect to database.");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe SQL server is down?");
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
	public SqlConnexion(Inventory2SQL plugin, String host, int port, String dbname, String login, String password)
	{
		this.plugin = plugin;
		try
		{
			this.sqlConnexion = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dbname, login, password);
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[Inventory2SQL] " + ChatColor.RED + "Unable to connect to database.");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe SQL server is down?");
			this.plugin.setPluginEnabled(false);
		}
	}
	
	/* ----- Actions ----- */
	/**
	 * Create the inventory table for save and load inventories from
	 */
	public void createTable()
	{
		try
		{
			Statement s = this.sqlConnexion.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS inventories (player varchar(32) NOT NULL, inventory TEXT NOT NULL, armor TEXT NOT NULL, enderchest TEXT NOT NULL, level int(11) NOT NULL, exp float NOT NULL, PRIMARY KEY (player))");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] SQL server is " + ChatColor.GREEN + "[ON]");
		}
		catch (SQLException e)
		{
			this.plugin.getConsole().sendMessage("[Inventory2SQL] SQL server is " + ChatColor.RED + "[OFF]");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe an error in the config file?");
			this.plugin.getConsole().sendMessage("[Inventory2SQL] Maybe SQL server is down?");
		}
	}
	
	/**
	 * Select informations from database
	 * @param playerName
	 * 		The player name
	 * @return A result set with found informations, false is nothing was found
	 * @throws SQLException
	 */
	public ResultSet selectInventory(String playerName) throws SQLException
	{
		PreparedStatement stmt = sqlConnexion.prepareStatement("SELECT * FROM inventories WHERE player= ?");
		stmt.setString(1, playerName);
		return stmt.executeQuery();
	}
	
	private void insertInventory(String playerName, String inventory, String armor, String enderchest, int level, float exp) throws SQLException
	{
		PreparedStatement s = this.sqlConnexion.prepareStatement("INSERT INTO inventories(player,inventory,armor,enderchest,level,exp) VAlUES(?,?,?,?,?,?)");
		s.setString(1, playerName);
		s.setString(2, inventory);
		s.setString(3, armor);
		s.setString(4, enderchest);
		s.setInt(5, level);
		s.setFloat(6, exp);
		s.executeUpdate();
	}
	
	private void updateInventory(String playerName, String inventory, String armor, String enderchest, int level, float exp) throws SQLException
	{
		PreparedStatement s = this.sqlConnexion.prepareStatement("UPDATE inventories SET inventory= ?,armor= ?,enderchest= ?,level= ?,exp= ? WHERE player= ?");
		s.setString(1, inventory);
		s.setString(2, armor);
		s.setString(3, enderchest);
		s.setInt(4, level);
		s.setFloat(5, exp);
		s.setString(6, playerName);
		s.executeUpdate();
	}
	
	/**
	 * Save player inventory, armor, enderchest, level and exp into database, or update it if exists
	 * @param playerName
	 * 		The player name
	 * @param inventory
	 * 		The JSON invetory
	 * @param armor
	 * 		The JSON armor
	 * @param enderchest
	 * 		The JSON enderchest
	 * @param level
	 * 		The amount of levels
	 * @param exp
	 * 		The player exp
	 * @throws SQLException
	 */
	public void saveInventory(String playerName, String inventory, String armor, String enderchest, int level, float exp) throws SQLException
	{
		if (this.selectInventory(playerName).next() != false)
		{
			this.updateInventory(playerName, inventory, armor, enderchest, level, exp);
		}
		else
		{
			this.insertInventory(playerName, inventory, armor, enderchest, level, exp);
		}
	}
	
	/**
	 * Load inventories from database for a player
	 * @param player
	 * 		The player who receive inventories
	 * @param rs
	 * 		The resultset with informations
	 * @throws JSONException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public void loadInventory(Player player, ResultSet rs) throws JSONException, IllegalArgumentException, SQLException
	{
		JsonDeserializer jd = new JsonDeserializer();
		
		player.getInventory().setContents(jd.deserializeInventory(rs.getString("inventory")));
		player.getInventory().setArmorContents(jd.deserializeInventory(rs.getString("armor")));
		player.getEnderChest().setContents(jd.deserializeInventory(rs.getString("enderchest")));
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