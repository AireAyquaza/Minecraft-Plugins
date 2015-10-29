/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * @description Allow the players to disenchant items.
 */
package aireayquaza.disenchanter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import aireayquaza.disenchanter.commands.*;

public class Disenchanter extends JavaPlugin
{
	private ConsoleCommandSender console;
	
	/**
	 * Enable the plugin
	 */
	@Override
	public void onEnable()
	{
		console = Bukkit.getServer().getConsoleSender();
	}
	
	/**
	 * Disable the plugin
	 */
	@Override
	public void onDisable()
	{
		console.sendMessage("[Disenchanter] Disabling Disenchanter ...");
	}
	
	/**
	 * Define the actions when a command is type
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		//Disenchant command
		if (label.equalsIgnoreCase("disenchant") && senderIsPlayer(sender))
		{
			Player player = (Player) sender;
			AbstractCommand c = new DisenchantCommand(player);
			c.execute();
		}
		
		//Separate command
		if (label.equalsIgnoreCase("separate") && senderIsPlayer(sender))
		{
			Player player = (Player) sender;
			AbstractCommand c = new SeparateCommand(player);
			c.execute();
		}
		
		return false;
	}
	
	/*
	 * Test id the sender of the command is a player
	 * @param sender
	 * 		The entity to test
	 * @return <code>true</code> if is a player, false else
	 */
	private boolean senderIsPlayer(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			return true;
		}
		else
		{
			console.sendMessage(ChatColor.RED + "Only players can perform this command!");
			return false;
		}
	}
}