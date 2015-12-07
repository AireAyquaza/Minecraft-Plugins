package aireayquaza.autocobblegenerator.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import aireayquaza.autocobblegenerator.AutoCobbleGenerator;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * Remove all existing auto-breakers
 */
public class RemoveAllCommand extends AbstractCommand
{
	/**
	 * @param p
	 * 		The player who type the command
	 * @param plugin
	 * 		The plugin where the command is defined
	 */
	public RemoveAllCommand(Player p, AutoCobbleGenerator plugin)
	{
		super(p, plugin);
	}
	
	/**
	 * Define what is done when the command is executed
	 */
	@Override
	public void execute()
	{
		for (Location key : this.plugin.getScheduledTasks().keySet())
		{
			if (this.plugin.getScheduledTasks().get(key) != null)
			{
				this.plugin.getScheduledTasks().get(key).cancel();
				this.player.sendMessage("[AutoCobbleGenerator] You removed the auto-breaker at X: " + key.getBlockX() + " Y: " + key.getBlockY() + " Z: " + key.getBlockZ());
			}
		}
		
		this.plugin.getScheduledTasks().clear();
	}
}