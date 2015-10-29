/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * @description Represents the disenchant command.
 */
package aireayquaza.disenchanter.commands;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.Repairable;

import aireayquaza.disenchanter.EnchantmentCost;

public class DisenchantCommand extends AbstractCommand
{
	private Player player;
	private ItemStack itemInHand;
	
	/**
	 * DisenchantCommand constructor
	 * @param player
	 * 		The command sender
	 */
	public DisenchantCommand(Player player, FileConfiguration config)
	{
		super(player, config);
		this.player = player;
		this.itemInHand = player.getItemInHand();
		this.config = config;
	}
	
	/**
	 * Execute command
	 */
	public void execute()
	{
		if (this.itemInHand != null && this.playerHasPermissionOrIsOp() && this.blockBelowPlayerIsEnchantingTable() && this.itemInHandHasEnchant() && this.playerHasBook(1))
		{
			this.extractEnchantmentAndGiveEnchantedBook();
		}
	}
	
	/* ----- Refactor ----- */
	/*
	 * Extract an enchantment from the player handed item, remove it from the item and place it
	 * into an enchanted book. This book is added into the player's inventory
	 */
	private void extractEnchantmentAndGiveEnchantedBook()
	{
		Entry<Enchantment,Integer> entry = this.itemInHand.getEnchantments().entrySet().iterator().next();
		int cost = this.getCost(entry.getKey(), entry.getValue());
		
		if (this.player.getLevel() >= cost)
		{
			ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
			
			meta.addStoredEnchant(entry.getKey(), entry.getValue(), false);
			enchantedBook.setItemMeta(meta);
			
			this.itemInHand.removeEnchantment(entry.getKey());
			this.findAndRemoveBook();
			this.player.getLocation().getWorld().dropItemNaturally(this.player.getLocation(), enchantedBook);
			this.player.sendMessage(ChatColor.GREEN + this.config.getString("disenchanter.disenchant.disenchantSuccess"));
			this.player.setLevel(this.player.getLevel() - cost);
			this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.LEVEL_UP, 100, 3);
			this.damageItem();
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.general.levelRequirementError").replaceAll("(.+)?(\\{levelCost\\})(.+)?", "$1" + cost + " level" + (cost > 1 ? "s" : "") + "$3"));
		}
	}
	
	/*
	 * Get the cost in level for execute the command
	 * @return the cost
	 */
	private int getCost(Enchantment ench, int level)
	{
		Repairable meta = (Repairable) this.itemInHand.getItemMeta();
		
		return EnchantmentCost.valueOf(ench.getName()).getCost() * level + meta.getRepairCost();
	}
	
	/* ----- Conditions ----- */
	/*
	 * Test if the player's item in hand has enchants
	 * @return <code>true</code> if has, false else
	 */
	protected boolean itemInHandHasEnchant()
	{
		if (this.itemInHand != null && this.itemInHand.hasItemMeta() && this.itemInHand.getItemMeta().hasEnchants())
		{
			return true;
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.disenchant.noEnchantmentItemInHandError"));
			return false;
		}
	}
}
