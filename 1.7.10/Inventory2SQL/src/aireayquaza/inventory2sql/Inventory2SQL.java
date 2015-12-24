package aireayquaza.inventory2sql;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import aireayquaza.inventory2sql.event.PlayerEvent;
import aireayquaza.inventory2sql.sql.SqlConnexion;

public class Inventory2SQL extends JavaPlugin
{
	private SqlConnexion bdd;
	private ConsoleCommandSender console;
	
	@Override
    public void onEnable()
	{
		this.console = Bukkit.getServer().getConsoleSender();
		this.console.sendMessage("[Inventory2SQL] Author: Aire_Ayquaza");
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
		
		String host, port, dbname, url, login, password;
		host = this.getConfig().getString("mySql.host");
		port = this.getConfig().getString("mySql.port");
		dbname = this.getConfig().getString("mySql.dbname");
		login = this.getConfig().getString("mySql.login");
		password = this.getConfig().getString("mySql.password");
		if (!(null != password))
			password = "";
		
		url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
		
		this.bdd = new SqlConnexion(this, url, login, password);
    }
	
	@Override
    public void onDisable()
	{
		this.getLogger().info("Disable Inventory2SQL");
		this.saveConfig();
		if (this.bdd != null)
			this.bdd.closeConnexion();
    }
	
	/* ----- Getters ----- */
	public SqlConnexion getConnection()
	{
		return this.bdd;
	}
	
	public ConsoleCommandSender getConsole()
	{
		return this.console;
	}
	
	/* ----- Setters ----- */
	public void setPluginEnabled(boolean b)
	{
		this.setEnabled(b);
	}
}