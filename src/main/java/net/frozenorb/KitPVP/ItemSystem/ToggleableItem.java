package net.frozenorb.KitPVP.ItemSystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.frozenorb.Utilities.Types.Attributes;
import net.minecraft.server.v1_6_R3.NBTTagCompound;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToggleableItem {
	private static final String SPACE = "       ";
	private ArrayList<Material> materials;
	private ArrayList<String> types;
	private int currentSelection = 0;
	private String displayName;
	private String lore;
	private ArrayList<Integer> data = new ArrayList<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			add(0);
		}
	};

	/**
	 * Creates a ToggleableItem
	 * 
	 * @param displayName
	 *            name of the item
	 * @param lore
	 *            lore of the item
	 * @param materials
	 *            the materials to switch between
	 * @param types
	 *            names to switch between
	 */
	public ToggleableItem(String displayName, String lore, ArrayList<Material> materials, ArrayList<String> types) {
		currentSelection = 0;
		this.materials = materials;
		this.displayName = displayName;
		this.types = types;
		this.lore = lore;
	}

	/**
	 * Creates a ToggleableItem using a map
	 * 
	 * @param displayName
	 *            name of the item
	 * @param lore
	 *            lore of the item
	 * @param map
	 *            string mapped to material
	 */
	public ToggleableItem(String displayName, String lore, LinkedHashMap<String, Material> map) {
		this.displayName = displayName;
		currentSelection = 0;
		this.materials = new ArrayList<Material>(map.values());
		this.types = new ArrayList<String>(map.keySet());
		this.lore = lore;
	}

	/**
	 * Creates a simple on/off ToggleableItem
	 * 
	 * @param displayName
	 *            name of the item
	 * @param lore
	 *            lore of the item
	 * @param first
	 *            first item to display
	 * @param second
	 *            second item
	 * @param defaultOn
	 *            true if the item should default to enabled
	 */
	public ToggleableItem(String displayName, String lore, final Material first, final Material second, final boolean defaultOn) {
		this.displayName = displayName;
		currentSelection = 0;
		this.materials = new ArrayList<Material>() {
			private static final long serialVersionUID = 1L;

			{
				add(first);
				add(second);
			}
		};
		this.types = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				if (defaultOn) {
					add("§a§lEnabled");
					add("§c§lDisabled");
				} else {
					add("§c§lDisabled");
					add("§a§lEnabled");
				}

			}
		};
		this.lore = lore;
	}

	public ToggleableItem setData(ArrayList<Integer> data) {
		this.data = data;
		return this;
	}

	public String getCurrentValue() {
		if (currentSelection == 0)
			return ChatColor.stripColor(types.get(types.size() - 1)).trim();
		return ChatColor.stripColor(types.get(currentSelection - 1)).trim();

	}

	public ItemStack initialize() {
		try {
			ItemStack item = new ItemStack(materials.get(0));
			ItemMeta meta = item.getItemMeta();
			meta.setLore(wrap(lore));
			meta.setDisplayName(displayName + SPACE + types.get(0));
			item.setItemMeta(meta);
			item.setDurability((short) (int) data.get(currentSelection));
			currentSelection += 1;
			if (currentSelection == materials.size())
				currentSelection = 0;
			Attributes attr = new Attributes(item);
			attr.clear();
			CraftItemStack cis = (CraftItemStack) item;
			Field f = cis.getClass().getDeclaredField("handle");
			f.setAccessible(true);
			net.minecraft.server.v1_6_R3.ItemStack is = (net.minecraft.server.v1_6_R3.ItemStack) f.get(cis);
			NBTTagCompound comp = is.getTag();
			comp.remove("CustomPotionEffects");
			is.setTag(comp);
			item = CraftItemStack.asBukkitCopy(is);
			return item;
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;

	}

	public boolean isThis(ItemStack item) {
		if (item.hasItemMeta()) {
			String name = item.getItemMeta().getDisplayName();
			if (ChatColor.stripColor(name).startsWith(ChatColor.stripColor(displayName)))
				return true;
		}
		return false;
	}

	public void next(ItemStack item) {
		if (item.hasItemMeta()) {
			try {
				ItemMeta meta = item.getItemMeta();
				meta.setLore(wrap(lore));
				item.setDurability((short) (int) data.get((currentSelection >= data.size() ? data.size() - 1 : currentSelection)));
				item.setType(materials.get(currentSelection));
				meta.setDisplayName(displayName + SPACE + types.get(currentSelection));
				item.setItemMeta(meta);
				Attributes attr = new Attributes(item);
				attr.clear();
				CraftItemStack cis = (CraftItemStack) item;
				Field f = cis.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				net.minecraft.server.v1_6_R3.ItemStack is = (net.minecraft.server.v1_6_R3.ItemStack) f.get(cis);
				NBTTagCompound comp = is.getTag();
				comp.remove("CustomPotionEffects");
				is.setTag(comp);
				item = CraftItemStack.asBukkitCopy(is);
				currentSelection += 1;
				if (currentSelection == materials.size())
					currentSelection = 0;
			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> wrap(String string) {
		String[] split = string.split(" ");
		string = "";
		ChatColor color = ChatColor.BLUE;
		ArrayList<String> newString = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			if (string.length() > 26 || string.endsWith(".") || string.endsWith("!")) {
				newString.add(color + string);
				if (string.endsWith(".") || string.endsWith("!"))
					newString.add("");
				string = "";
			}
			string += (string.length() == 0 ? "" : " ") + split[i];
		}
		newString.add(color + string);
		return newString;
	}
}