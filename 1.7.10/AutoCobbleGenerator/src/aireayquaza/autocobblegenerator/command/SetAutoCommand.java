package aireayquaza.autocobblegenerator.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import aireayquaza.autocobblegenerator.AutoCobbleGenerator;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * Set-up an existing auto-breaker
 */
public class SetAutoCommand extends AbstractCommand
{
	protected Runnable runnable = new AutoCobbleRunnable();
	
	/**
	 * @param p
	 * 		The player who type the command
	 * @param plugin
	 * 		The plugin where the command is defined
	 */
	public SetAutoCommand(Player p, AutoCobbleGenerator plugin)
	{
		super(p, plugin);
		p.sendMessage("[AutoCobbleGenerator] " + "You setup an auto-breaker at X: " + this.loc.getBlockX() + " Y: " + this.loc.getBlockY() + " Z: " + this.loc.getBlockZ());
	}
	
	/**
	 * Define what is done when the command is executed
	 */
	@Override
	public void execute()
	{
		if (this.loc.getBlock().getType().equals(Material.COBBLESTONE))
		{
			BukkitTask task = Bukkit.getScheduler().runTaskTimer(this.plugin, this.runnable, 0L, 10L);
			this.plugin.getScheduledTasks().put(this.loc, task);
		}
		else
		{
			this.player.sendMessage("You need to look at a Cobblestone block to setup the breaker!");
		}
	}
	
	/**
	 * @author Aire Ayquaza
	 * @version 1.0.0
	 * Represents the timed-task
	 */
	protected class AutoCobbleRunnable implements Runnable
	{
		public AutoCobbleRunnable(){}
		
		public void run()
		{
			if (SetAutoCommand.this.loc.getBlock().getType().equals(Material.COBBLESTONE))
			{
				SetAutoCommand.this.loc.getBlock().breakNaturally();
				SetAutoCommand.this.loc.getWorld().playSound(SetAutoCommand.this.loc, Sound.DIG_STONE, 10f, 1f);
			}
		}
	}
}