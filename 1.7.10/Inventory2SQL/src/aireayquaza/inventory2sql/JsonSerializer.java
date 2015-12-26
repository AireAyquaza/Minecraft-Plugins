package aireayquaza.inventory2sql;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * The class who convert ItemStack[] -> JSON
 */
public class JsonSerializer
{
	private ItemStack item;
	private String minecraftSerialized;
	
	private JsonSerializer(ItemStack item)
	{
		this.item = item;
		this.minecraftSerialized = item.serialize().toString();
	}
	
	/* ----- Statics ----- */
	/**
	 * Convert into JSON text the specified inventory
	 * @param inventory
	 * 		The inventory to convert to JSON
	 * @return The JSON format of the inventory
	 */
	public static String serializeInventory(ItemStack[] inventory)
	{
		String serializedInventory = "";
		
		for (int i = 0; i < inventory.length; i++)
		{
			if (inventory[i] != null && inventory[i].getDurability() >= 0)
			{
				JsonSerializer js = new JsonSerializer(inventory[i]);
				serializedInventory += js.serialize() + ",";
			}
			else
			{
				serializedInventory += "{\"type\": \"AIR\",\"damage\": -1,\"amount\": 0},";
			}
		}
		
		return "{\"content\": [" + serializedInventory.substring(0, serializedInventory.length() -1) + "]}";
	}
	
	/* ----- Getters ----- */
	private String getMetaType()
	{
		if (this.item.hasItemMeta())
		{
			String pattern = ".+meta=([A-Z_]+).+";
			
			return this.minecraftSerialized.replaceAll(pattern, "$1");
		}
		else
		{
			return "";
		}
	}
	
	/* ----- Actions ----- */
	private String serialize()
	{
		switch (this.getMetaType())
		{
			case "SKULL_META":
			case "UNSPECIFIC_META":
			case "ENCHANTED_META":
				this.serializeUnspecific();
				break;
			case "BOOK_META":
				this.serializeWrittenBook();
				break;
			case "FIREWORK_EFFECT_META":
				this.serializeFireworkEffect();
				break;
			case "FIREWORK_META":
				this.serializeFirework();
				break;
			case "LEATHER_ARMOR_META":
				this.serializeLeatherArmor();
				break;
			default:
				this.serializeNoMeta();
		}
		
		return this.minecraftSerialized;
	}
	
	/* ------ Serialization ----- */
	private void serializeNoMeta()
	{
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("=", ": ");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("([a-zA-Z_-]+):", "\"$1\":");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll(", ", ",");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll(": (([A-Za-z_-]| )+),", ": \"$1\",");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll(": (([A-Za-z_-]| )+)", ": \"$1\"");
	}
	
	private void serializeUnspecific()
	{
		if (this.item.getItemMeta().hasLore())
		{
			String lore = "";
			
			for (String s : this.item.getItemMeta().getLore())
			{
				lore += s + ";";
			}
			
			lore = lore.substring(0, lore.length() - 1);
			this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+lore=\\[)(.+)(\\].+)", "$1\"" + lore + "\"$3");
		}
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+display-name=)([^,]+)(.+)", "$1\"$2\"$3");
		this.serializeNoMeta();
		this.minecraftSerialized = this.minecraftSerialized.replaceAll(":\\{", ": {");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+)\"meta\": \"([A-Z_-]+)\": \\{(.+)", "$1\"meta-type\": \"$2\",\"meta\": {$3");
		String lore = this.minecraftSerialized.replaceAll("(.+\"lore\": \\[\")(.+)(\"\\].+)", "$2").replaceAll("\"", "");
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+\"lore\": \\[\")(.+)(\"\\].+)", "$1" + lore + "$3");
	}
	
	private void serializeLeatherArmor()
	{
		this.serializeUnspecific();
		
		LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
		int color = lam.getColor().asRGB();
		this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+\"color\": )\"Color\":\\[rgb0x([0-9ABCDEF]{6})\\](.+)", "$1[" + color + "]$3");
	}
	
	private void serializeWrittenBook()
	{
		this.serializeUnspecific();
		BookMeta bm = (BookMeta) this.item.getItemMeta();
		
		if (bm.hasPages())
		{
			String pages = "";
			for (String page : bm.getPages())
			{
				pages += page + ";";
			}
			
			pages = pages.substring(0, pages.length() - 1);
			
			this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+\"pages\": )(.+)(.+)", "$1\"" + pages + "\"}$3");
		}
	}
	
	private void serializeFireworkEffect()
	{
		FireworkEffectMeta fem = (FireworkEffectMeta) this.item.getItemMeta();
		
		if (fem.hasEffect())
		{
			String effect = "";
			effect += "\"flicker\": " + fem.getEffect().hasFlicker() + ", ";
			effect += "\"trail\": " + fem.getEffect().hasTrail() + ", ";
			effect += "\"colors\": [";
			if (fem.getEffect().getColors().size() > 0)
			{
				for (Color color : fem.getEffect().getColors())
				{
					effect += String.valueOf(color.asRGB()) + ", ";
				}
				effect = effect.substring(0, effect.length() - 2);
			}
			effect += "], ";
			effect += "\"fade-colors\": [";
			if (fem.getEffect().getFadeColors().size() > 0)
			{
				for (Color color : fem.getEffect().getFadeColors())
				{
					effect += String.valueOf(color.asRGB()) + ", ";
				}
				effect = effect.substring(0, effect.length() - 2);
			}
			effect += "], ";
			effect += "\"type\": " + fem.getEffect().getType().name();
			
			this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+firework-effect=)FireworkEffect:\\{([^}]+)(.+)", "$1{" + effect + "$3");
		}
		this.serializeUnspecific();
	}
	
	private void serializeFirework()
	{
		FireworkMeta fm = (FireworkMeta) this.item.getItemMeta();
		
		if (fm.hasEffects())
		{
			String effects = "";
			
			for (FireworkEffect e : fm.getEffects())
			{
				String effect = "";
				effect += "\"flicker\": " + e.hasFlicker() + ", ";
				effect += "\"trail\": " + e.hasTrail() + ", ";
				effect += "\"colors\": [";
				if (e.getColors().size() > 0)
				{
					for (Color color :e.getColors())
					{
						effect += String.valueOf(color.asRGB()) + ", ";
					}
					effect = effect.substring(0, effect.length() - 2);
				}
				effect += "], ";
				effect += "\"fade-colors\": [";
				if (e.getFadeColors().size() > 0)
				{
					for (Color color : e.getFadeColors())
					{
						effect += String.valueOf(color.asRGB()) + ", ";
					}
					effect = effect.substring(0, effect.length() - 2);
				}
				effect += "], ";
				effect += "\"type\": " + e.getType().name();
				
				effects += "{" + effect + "}, ";
			}
			
			effects = effects.substring(0, effects.length() - 2);
			
			this.minecraftSerialized = this.minecraftSerialized.replaceAll("(.+firework-effects=\\[)(.+)(\\].+)", "$1" + effects + "$3");
		}
		this.serializeUnspecific();
	}
}