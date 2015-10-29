/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * @description Represents the separate command.
 */
package aireayquaza.disenchanter.commands;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import aireayquaza.disenchanter.EnchantmentCost;

public class SeparateCommand extends AbstractCommand
{
	/**
	 * SeparateCommand constructor
	 * @param player
	 * 		The command sender
	 */
	public SeparateCommand(Player player, FileConfiguration config)
	{
		super(player, config);
		this.player = player;
		this.itemInHand = player.getItemInHand();
		this.config = config;
	}

	/**
	 * Execute command
	 */
	@Override
	public void execute()
	{
		if (this.itemInHand != null && this.playerHasPermissionOrIsOp() && this.blockBelowPlayerIsEnchantingTable() && this.itemInHandIsEnchantedBook() && this.playerHasBook(((EnchantmentStorageMeta) this.itemInHand.getItemMeta()).getStoredEnchants().size()))
		{
			this.extractEnchantmentAndGiveEnchantedBook();
		}
	}
	
	/* ----- Refactor ----- */
	/*
	 * Extract and give all enchantment who are in the item in hand
	 */
	private void extractEnchantmentAndGiveEnchantedBook()
	{
		int cost = this.getCost();
		
		if (this.player.getLevel() >= cost)
		{
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.itemInHand.getItemMeta();
			
			for (Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet())
			{
				ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
				EnchantmentStorageMeta m = (EnchantmentStorageMeta) item.getItemMeta();
				m.addStoredEnchant(entry.getKey(), entry.getValue(), false);
				item.setItemMeta(m);
				
				this.player.getLocation().getWorld().dropItemNaturally(this.player.getLocation(), item);
				this.findAndRemoveBook();
			}
			
			this.player.setItemInHand(null);
			this.player.setLevel(this.player.getLevel() - cost);
			this.player.getLocation().getWorld().playSound(this.player.getLocation(), Sound.LEVEL_UP, 100, 3);
			this.player.sendMessage(ChatColor.GREEN + this.config.getString("disenchanter.separate.separateSuccess"));
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
	private int getCost()
	{
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.itemInHand.getItemMeta();
		
		Set<Entry<Enchantment, Integer>> set = meta.getStoredEnchants().entrySet();
		Iterator<Entry<Enchantment, Integer>> it = set.iterator();
		
		int cost = 0;
		
		while (it.hasNext())
		{
			Entry<Enchantment, Integer> entry = it.next();
			
			cost += EnchantmentCost.valueOf(entry.getKey().getName()).getCost() * entry.getValue();
		}
		
		return cost;
	}
	
	/* ----- Conditions ----- */
	/*
	 * Test if the item in hand is an enchanted book or not
	 * @return <code>true</code> if is, false else
	 */
	private boolean itemInHandIsEnchantedBook()
	{
		if (this.itemInHand != null && this.itemInHand.getType().equals(Material.ENCHANTED_BOOK))
		{
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.itemInHand.getItemMeta();
			if (meta.getStoredEnchants().size() > 1)
			{
				return true;
			}
		}
		this.player.sendMessage(ChatColor.RED + this.config.getString("disenchanter.separate.itemInHandNotAnEnchantedBookError"));
		return false;
	}
}