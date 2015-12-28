package aireayquaza.inventory2sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.comphenix.example.Attributes;
import com.comphenix.example.Attributes.Attribute;
import com.comphenix.example.Attributes.AttributeType;
import com.comphenix.example.Attributes.Operation;

/**
 * @author Aire Ayquaza
 * @version 1.0.0
 * The class who convert JSON -> ItemStack[]
 */
public class JsonDeserializer
{
	public JsonDeserializer(){}
	
	/* ----- Actions ----- */
	/**
	 * Convert JSON ItemStacks to Java ItemStacks
	 * @param jsonInventory
	 * 		The JSON text inventory
	 * @return The ItemStack array inventory
	 * @throws JSONException
	 */
	public ItemStack[] deserializeInventory(String jsonInventory) throws JSONException
	{
		JSONObject json = new JSONObject(jsonInventory);
		JSONArray content = json.getJSONArray("content");
		ItemStack[] inventory = new ItemStack[content.length()];
		
		for (int i = 0; i < content.length(); i++)
		{
			inventory[i] = this.deserializeItem(content.getJSONObject(i));
		}
		
		return inventory;
	}
	
	private ItemStack deserializeItem(JSONObject source) throws JSONException
	{
		if (source.has("meta-type"))
		{
			switch (source.getString("meta-type"))
			{
				case "SKULL_META":
					return this.deserializeSkull(source);
				case "UNSPECIFIC_META":
					return this.deserializeUnspecific(source);
				case "ENCHANTED_META":
					return this.deserializeEnchanted(source);
				case "BOOK_META":
					return this.deserializeWrittenBook(source);
				case "FIREWORK_EFFECT_META":
					return this.deserializeFireworkEffect(source);
				case "FIREWORK_META":
					return this.deserializeFirework(source);
				case "LEATHER_ARMOR_META":
					return this.deserializeLeatherArmor(source);
				default:
					return this.deserializeNoMeta(source);
			}
		}
		else
		{
			return this.deserializeNoMeta(source);
		}
	}
	
	private ItemStack deserializeNoMeta(JSONObject source) throws JSONException
	{
		ItemStack item = new ItemStack(Material.valueOf(source.getString("type")));
		
		if (source.has("damage"))
			item.setDurability(Short.parseShort(String.valueOf(source.getInt("damage"))));
		
		if (source.has("amount"))
			item.setAmount(source.getInt("amount"));
		
		if (source.has("attributes"))
		{
			JSONArray jsonAttr = source.getJSONArray("attributes");
			
			Attributes attributes = new Attributes(item);
			
			for (int i = 0; i < jsonAttr.length(); i++)
			{
				JSONObject attr = jsonAttr.getJSONObject(i);
				attributes.add(Attribute.newBuilder().name(attr.getString("name")).type(AttributeType.fromId(attr.getString("id"))).amount(attr.getDouble("amount")).uuid(UUID.fromString(attr.getString("UUID"))).operation(Operation.fromId(attr.getInt("operationId"))).build());
			}
			
			item = attributes.getStack();
		}
		
		return item;
	}
	
	private ItemStack deserializeUnspecific(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeNoMeta(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("display-name"))
		{
			ItemMeta m = item.getItemMeta();
			m.setDisplayName(meta.getString("display-name"));
			
			item.setItemMeta(m);
		}
		
		if (meta.has("lore"))
		{
			ItemMeta m = item.getItemMeta();
			m.setLore(Arrays.asList(meta.getJSONArray("lore").getString(0).split(";")));
			
			item.setItemMeta(m);
		}
		
		if (meta.has("enchants"))
		{
			JSONObject json = meta.getJSONObject("enchants");
			
			Iterator<String> it = json.keys();
			while (it.hasNext())
			{
				String ench = it.next();
				item.addUnsafeEnchantment(Enchantment.getByName(ench), json.getInt(ench));
			}
		}
		
		if (meta.has("repair-cost"))
		{
			Repairable rm = (Repairable) item.getItemMeta();
			rm.setRepairCost(meta.getInt("repair-cost"));
			
			item.setItemMeta((ItemMeta) rm);
		}
		
		return item;
	}
	
	private ItemStack deserializeSkull(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeUnspecific(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("skull-owner"))
		{
			SkullMeta sm = (SkullMeta) item.getItemMeta();
			sm.setOwner(meta.getString("skull-owner"));
			
			item.setItemMeta(sm);
		}
		
		return item;
	}
	
	private ItemStack deserializeEnchanted(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeNoMeta(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("display-name"))
		{
			ItemMeta m = item.getItemMeta();
			m.setDisplayName(meta.getString("display-name"));
			
			item.setItemMeta(m);
		}
		
		if (meta.has("lore"))
		{
			ItemMeta m = item.getItemMeta();
			m.setLore(Arrays.asList(meta.getJSONArray("lore").getString(0).split(";")));
			
			item.setItemMeta(m);
		}
		
		if (meta.has("stored-enchants"))
		{
			EnchantmentStorageMeta em = (EnchantmentStorageMeta) item.getItemMeta();
			JSONObject json = meta.getJSONObject("stored-enchants");
			
			Iterator<String> it = json.keys();
			while (it.hasNext())
			{
				String ench = it.next();
				em.addStoredEnchant(Enchantment.getByName(ench), json.getInt(ench), true);
			}
			
			item.setItemMeta(em);
		}
		
		if (meta.has("repair-cost"))
		{
			Repairable rm = (Repairable) item.getItemMeta();
			rm.setRepairCost(meta.getInt("repair-cost"));
			
			item.setItemMeta((ItemMeta) rm);
		}
		
		return item;
	}
	
	private ItemStack deserializeWrittenBook(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeUnspecific(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("author"))
		{
			BookMeta bm = (BookMeta) item.getItemMeta();
			bm.setAuthor(meta.getString("author"));
			
			item.setItemMeta(bm);
		}
		
		if (meta.has("title"))
		{
			BookMeta bm = (BookMeta) item.getItemMeta();
			bm.setTitle(meta.getString("title").replaceAll("_"," "));
			
			item.setItemMeta(bm);
		}
		
		if (meta.has("pages"))
		{
			BookMeta bm = (BookMeta) item.getItemMeta();
			bm.setPages(Arrays.asList(meta.getString("pages").split(";")));
			
			item.setItemMeta(bm);
		}
		
		return item;
	}
	
	private ItemStack deserializeLeatherArmor(JSONObject source) throws NumberFormatException, IllegalArgumentException, JSONException
	{
		ItemStack item = this.deserializeUnspecific(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("color"))
		{
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			lam.setColor(Color.fromRGB(meta.getJSONArray("color").getInt(0)));
			
			item.setItemMeta(lam);
		}
		
		return item;
	}
	
	private ItemStack deserializeFireworkEffect(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeUnspecific(source);
		
		JSONObject meta = source.getJSONObject("meta");
		
		if (meta.has("firework-effect"))
		{
			JSONObject json = meta.getJSONObject("firework-effect");
			
			boolean flicker = Boolean.parseBoolean(json.getString("flicker"));
			boolean trail = Boolean.parseBoolean(json.getString("trail"));
			List<Color> colors = new ArrayList<Color>();
			List<Color> fadeColors = new ArrayList<Color>();
			FireworkEffect.Type type = FireworkEffect.Type.valueOf(json.getString("type"));
			
			JSONArray jsonColor = json.getJSONArray("colors");
			for (int i = 0; i < jsonColor.length(); i++)
			{
				colors.add(Color.fromRGB(jsonColor.getInt(i)));
			}
			
			JSONArray jsonFadeColor = json.getJSONArray("fade-colors");
			for (int i = 0; i < jsonFadeColor.length(); i++)
			{
				fadeColors.add(Color.fromRGB(jsonFadeColor.getInt(i)));
			}
			
			FireworkEffectMeta fem = (FireworkEffectMeta) item.getItemMeta();
			fem.setEffect(this.buildEffect(flicker, trail, colors, fadeColors, type));
			item.setItemMeta(fem);
		}
		
		return item;
	}
	
	private ItemStack deserializeFirework(JSONObject source) throws JSONException
	{
		ItemStack item = this.deserializeUnspecific(source);
		FireworkMeta fm = (FireworkMeta) item.getItemMeta();
		
		JSONObject meta = source.getJSONObject("meta");
		
		fm.setPower(meta.getInt("power"));
		
		if (meta.has("firework-effects"))
		{
			JSONArray jsonEffectsArray = meta.getJSONArray("firework-effects");
			
			for (int i = 0; i < jsonEffectsArray.length(); i++)
			{
				JSONObject effect = jsonEffectsArray.getJSONObject(i);
				
				boolean flicker = effect.getBoolean("flicker");
				boolean trail = effect.getBoolean("trail");
				List<Color> colors = new ArrayList<Color>();
				List<Color> fadeColors = new ArrayList<Color>();
				FireworkEffect.Type type = FireworkEffect.Type.valueOf(effect.getString("type"));
				
				JSONArray jsonColor = effect.getJSONArray("colors");
				for (int j = 0; j < jsonColor.length(); j++)
				{
					colors.add(Color.fromRGB(jsonColor.getInt(j)));
				}
				
				JSONArray jsonFadeColor = effect.getJSONArray("fade-colors");
				for (int j = 0; j < jsonFadeColor.length(); j++)
				{
					fadeColors.add(Color.fromRGB(jsonFadeColor.getInt(j)));
				}
				
				fm.addEffect(this.buildEffect(flicker, trail, colors, fadeColors, type));
			}
		}
		
		item.setItemMeta(fm);
		
		return item;
	}
	
	private FireworkEffect buildEffect(boolean flicker, boolean trail, List<Color> colors, List<Color> fadeColors, FireworkEffect.Type type)
	{
		Builder fe = FireworkEffect.builder();
		
		if (flicker)
			fe.withFlicker();
		if (trail)
			fe.withTrail();
		for (Color c : colors)
			fe.withColor(c);
		for (Color c : fadeColors)
			fe.withFade(c);
		fe.with(type);
		
		return fe.build();
	}
}