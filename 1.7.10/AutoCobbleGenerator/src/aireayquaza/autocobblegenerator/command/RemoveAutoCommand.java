package aireayquaza.autocobblegenerator.command;

import org.bukkit.entity.Player;

import aireayquaza.autocobblegenerator.AutoCobbleGenerator;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * Remove an existing auto-breaker
 */
public class RemoveAutoCommand extends AbstractCommand
{
	/**
	 * @param p
	 * 		The player who type the command
	 * @param plugin
	 * 		The plugin where the command is defined
	 */
	public RemoveAutoCommand(Player p, AutoCobbleGenerator plugin)
	{
		super(p, plugin);
	}
	
	/**
	 * Define what is done when the command is executed
	 */
	@Override
	public void execute()
	{
		if (this.plugin.getScheduledTasks().get(this.loc) != null)
		{
			this.plugin.getScheduledTasks().get(this.loc).cancel();
			this.plugin.getScheduledTasks().remove(this.loc);
			this.player.sendMessage("[AutoCobbleGenerator] You removed the auto-breaker at X: " + this.loc.getBlockX() + " Y: " + this.loc.getBlockY() + " Z: " + this.loc.getBlockZ());
		}
	}
}