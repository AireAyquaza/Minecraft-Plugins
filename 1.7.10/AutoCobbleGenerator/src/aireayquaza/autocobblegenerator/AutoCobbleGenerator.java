package aireayquaza.autocobblegenerator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import aireayquaza.autocobblegenerator.command.AbstractCommand;
import aireayquaza.autocobblegenerator.command.RemoveAllCommand;
import aireayquaza.autocobblegenerator.command.RemoveAutoCommand;
import aireayquaza.autocobblegenerator.command.SetAutoCommand;

public class AutoCobbleGenerator extends JavaPlugin
{
	private ConsoleCommandSender console;
	private final Map<Location, BukkitTask> scheduledTasks = new HashMap<Location, BukkitTask>();
	
	public Map<Location, BukkitTask> getScheduledTasks()
	{
		return this.scheduledTasks;
	}
	
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
		console.sendMessage("[AutoCobblestoneGenerator] Disabling AutoCobblestoneGenerator ...");
	}
	
	/**
	 * Define the actions when a command is type
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (label.equalsIgnoreCase("setauto") && senderIsPlayer(sender))
		{
			Player player = (Player) sender;
			AbstractCommand c = new SetAutoCommand(player, this);
			c.execute();
		}
		
		if (label.equalsIgnoreCase("removeauto") && senderIsPlayer(sender))
		{
			Player player = (Player) sender;
			AbstractCommand c = new RemoveAutoCommand(player, this);
			c.execute();
		}
		
		if (label.equalsIgnoreCase("removeall") && senderIsPlayer(sender))
		{
			Player player = (Player) sender;
			AbstractCommand c = new RemoveAllCommand(player, this);
			c.execute();
		}
		
		return false;
	}
	
	/*
	 * Test id the sender of the command is a player
	 * @param sender
	 * 		The entity to test
	 * @return {@code true} if is a player, {@code false} else
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