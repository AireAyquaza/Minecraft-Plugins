/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * @description Represents an abstract command.
 */
package aireayquaza.disenchanter.commands;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractCommand
{
	protected Player player;
	protected ItemStack itemInHand;
	protected FileConfiguration config;
	
	/**
	 * Command constructor
	 * @param player
	 * 		The command sender
	 */
	public AbstractCommand(Player player, FileConfiguration config)
	{
		this.player = player;
		this.itemInHand = player.getItemInHand();
		this.config = config;
	}
	
	/**
	 * Execute the command
	 */
	public abstract void execute();
	
	/*
	 * Damage the item during the disenchantment
	 */
	protected void damageItem()
	{
		short actualDurability = this.itemInHand.getDurability();
		int newDurability = actualDurability + ((this.itemInHand.getType().getMaxDurability() * 20) / 100);
		
		if (newDurability <= 0)
		{
			this.player.setItemInHand(null);
			this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.ITEM_BREAK, 100, 1);
		}
		else
		{
			this.itemInHand.setDurability((short) newDurability);
		}
	}
	
	/* ----- Refactor ----- */
	/*
	 * Find the index of a normal book in player's inventory, decrease by 1 the amount or delete it from the player inventory
	 * @param player
	 * 		The player, command sender
	 */
	protected void findAndRemoveBook()
	{
		int i = 0;
		
		for (ItemStack item : player.getInventory())
		{
			if (item != null && item.getType().equals(Material.BOOK))
			{
				int amount = item.getAmount();
				
				if (amount > 1)
				{
					item.setAmount(amount - 1);
				}
				else
				{
					player.getInventory().setItem(i, null);
				}
			}
			
			i++;
		}
	}
	
	/*
	 * Get the amount of books in the player inventory
	 * @return the number
	 */
	protected int getAmountOfBook()
	{
		int n = 0;
		Iterator<ItemStack> it = this.player.getInventory().iterator();
		while (it.hasNext())
		{
			ItemStack item = it.next();
			
			if (item != null && item.getType().equals(Material.BOOK))
			{
				n += item.getAmount();
			}
		}
		
		return n;
	}
	
	/* ----- Conditions ----- */
	/*
	 * Test if the player has permission to use this command or if is Op
	 * @return <code>true</code> if has, false else
	 */
	protected boolean playerHasPermissionOrIsOp()
	{
		if (this.player.hasPermission("disenchanter.disenchant") || this.player.isOp())
		{
			return true;
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.general.playerHasPermissionError"));
			return false;
		}
	}
	
	/*
	 * Test if the player in on an enchanting table
	 * @return <code>true</code> if is, false else
	 */
	protected boolean blockBelowPlayerIsEnchantingTable()
	{
		if (this.player.getLocation().getBlock().getType().equals(Material.ENCHANTMENT_TABLE))
		{
			return true;
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.general.blockBelowIsNotEnchantingTableError"));
			return false;
		}
	}
	
	/*
	 * Test if the player inventory has nbOfBook books
	 * @param nbOfBook
	 * 		The required number of books
	 * @return <code>true</code> if has, false else
	 */
	protected boolean playerHasBook(int nbOfBook)
	{
		if (this.player.getInventory().contains(Material.BOOK))
		{
			if (this.getAmountOfBook() >= nbOfBook)
			{
				return true;
			}
			else
			{
				this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.general.multipleBookRequirementError").replaceAll("(.+)?(\\{nbOfBook\\})(.+)?", "$1" + nbOfBook + " book" + (nbOfBook > 1 ? "s" : "") + "$3"));
				return false;
			}
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.general.bookRequirementError"));
			return false;
		}
	}
}