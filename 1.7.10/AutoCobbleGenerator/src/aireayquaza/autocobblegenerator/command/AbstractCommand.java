package aireayquaza.autocobblegenerator.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import aireayquaza.autocobblegenerator.AutoCobbleGenerator;

public abstract class AbstractCommand
{
	protected Player player;
	protected AutoCobbleGenerator plugin;
	protected Location loc;
	
	/**
	 * @param p
	 * 		The player who type the command
	 * @param plugin
	 * 		The plugin where the command is defined
	 */
	@SuppressWarnings("deprecation")
	public AbstractCommand(Player p, AutoCobbleGenerator plugin)
	{
		this.player = p;
		this.plugin = plugin;
		this.loc = p.getTargetBlock(null, 100).getLocation();
	}
	
	/**
	 * Define what is done when the command is executed
	 */
	public abstract void execute();
}