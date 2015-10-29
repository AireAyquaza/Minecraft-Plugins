/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * @description Represents all level cost for each enchantment type.
 */
package aireayquaza.disenchanter;

public enum EnchantmentCost
{
	ARROW_DAMAGE(1),
	ARROW_FIRE(4),
	ARROW_INFINITE(8),
	ARROW_KNOCKBACK(2),
	DAMAGE_ALL(1),
	DAMAGE_ARTHROPODS(1),
	DAMAGE_UNDEAD(1),
	DIG_SPEED(1),
	DURABILITY(1),
	FIRE_ASPECT(6),
	KNOCKBACK(2),
	LOOT_BONUS_BLOCKS(8),
	LOOT_BOUS_MOBS(8),
	LUCK(6),
	LURE(4),
	OXYGEN(3),
	PROTECTION_ENVIRONMENTAL(1),
	PROTECTION_EXPLOSIONS(1),
	PROTECTION_FALL(4),
	PROTECTION_FIRE(1),
	PROTECTION_PROJECTILE(1),
	SILK_TOUCH(8),
	THORNS(8),
	WATER_WORKER(6);
	
	/**
	 * EnchantmentCost constructor
	 * @param cost
	 * 		The assigned cost
	 */
	EnchantmentCost(int cost)
	{
		this.cost = cost;
	}
	
	private int cost;
	
	/**
	 * Get the cost for this enchantment
	 * @return the cost
	 */
	public int getCost()
	{
		return this.cost;
	}
}