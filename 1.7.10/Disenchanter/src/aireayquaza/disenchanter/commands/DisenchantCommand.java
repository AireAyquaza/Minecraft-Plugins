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
	public DisenchantCommand(Player player)
	{
		super(player);
	}
	
	/**
	 * Execute command
	 */
	public void execute()
	{
		if (this.playerHasPermissionOrIsOp() && this.blockBelowPlayerIsEnchantingTable() && this.itemInHandHasEnchant() && this.playerHasBook(1))
		{
			this.extractEnchantmentAndGiveEnchantedBook();
			this.damageItem();
		}
	}
	
	/* ----- Refactor ----- */
	/*
	 * Extract an enchantment from the player handed item, remove it from the item and place it
	 * into an enchanted book. This book is added into the player's inventory
	 */
	private void extractEnchantmentAndGiveEnchantedBook()
	{
		int cost = this.getCost();
		
		if (this.player.getLevel() >= cost)
		{
			Entry<Enchantment,Integer> entry = this.itemInHand.getEnchantments().entrySet().iterator().next();
			ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
			
			meta.addStoredEnchant(entry.getKey(), entry.getValue(), false);
			enchantedBook.setItemMeta(meta);
			
			this.findAndRemoveBook();
			this.player.getLocation().getWorld().dropItemNaturally(this.player.getLocation(), enchantedBook);
			this.player.sendMessage(ChatColor.GREEN + "Disenchanting success!");
			this.player.setLevel(this.player.getLevel() - cost);
			this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.LEVEL_UP, 100, 3);
		}
		else
		{
			this.player.sendMessage(ChatColor.RED + "You need " + cost + " level" + (cost > 1 ? "s" : "") + " to disenchant this item!");
		}
	}
	
	/*
	 * Get the cost in level for execute the command
	 * @return the cost
	 */
	private int getCost()
	{
		Entry<Enchantment,Integer> entry = this.itemInHand.getEnchantments().entrySet().iterator().next();
		Repairable meta = (Repairable) this.itemInHand.getItemMeta();
		
		this.itemInHand.removeEnchantment(entry.getKey());
		
		return EnchantmentCost.valueOf(entry.getKey().getName()).getCost() * entry.getValue() + meta.getRepairCost();
	}
}
