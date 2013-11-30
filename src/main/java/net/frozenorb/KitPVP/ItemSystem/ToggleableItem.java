package net.frozenorb.KitPVP.ItemSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.frozenorb.KitPVP.MatchSystem.MatchRequest;
import net.frozenorb.Utilities.Types.Attributes;
import net.frozenorb.mBasic.SignShop.RomanNumeral;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToggleableItem {
	private ArrayList<Material> materials;
	private ArrayList<String> types;
	private int currentSelection = 0;
	private int currentSelectionSecondary = 0;
	private String displayName;
	private String lore;
	private ArrayList<Integer> secondaries = new ArrayList<Integer>();
	private String secondaryName;
	private String secondaryItemName;
	private Enchantment enchant;
	private boolean secondary = false;
	private boolean ignoreFirst = true;
	private boolean potionHeal = false;
	private boolean soupItem = false;
	private ArrayList<Integer> data = new ArrayList<Integer>() {
		private static final long serialVersionUID = 1L;

		{
			add(0);
		}
	};

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
		currentSelectionSecondary = 0;
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
		currentSelectionSecondary = 0;
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

	public ToggleableItem setSecondary(String name, ArrayList<Integer> secondaries, String secondaryItemName, Enchantment e) {
		this.enchant = e;
		this.secondaryName = name;
		this.secondaryItemName = secondaryItemName;
		this.secondaries = secondaries;
		this.secondary = true;
		return this;
	}

	public ToggleableItem setPotionHeal(boolean rf) {
		potionHeal = rf;
		return this;
	}

	public boolean isPotionHeal() {
		return potionHeal;
	}

	public void setIgnoreFirst(boolean ignoreFirst) {
		this.ignoreFirst = ignoreFirst;
	}

	public boolean isIgnoreFirst() {
		return ignoreFirst;
	}

	public ToggleableItem setData(ArrayList<Integer> data) {
		this.data = data;
		return this;
	}

	public String getCurrentPrimaryValue() {
		return ChatColor.stripColor(types.get(currentSelection)).trim();
	}

	public int getCurrentSecondaryValue() {
		return secondaries.get(currentSelectionSecondary);

	}

	public ItemStack initialize() {
		ItemStack item = new ItemStack(getMaterials().get(0));
		ItemMeta meta = item.getItemMeta();
		meta.setLore(getLore());
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		item.setDurability((short) (int) data.get(currentSelection));
		if (enchant != null && secondary) {
			if (!(currentSelectionSecondary == 0 && ignoreFirst))
				item.addUnsafeEnchantment(enchant, currentSelectionSecondary + (ignoreFirst ? 0 : 1));
			else
				item.removeEnchantment(enchant);
		}
		Attributes attr = new Attributes(item);
		attr.clear();
		return attr.getStack();

	}

	public boolean isThis(ItemStack item) {
		if (item.hasItemMeta()) {
			String name = item.getItemMeta().getDisplayName();
			if (ChatColor.stripColor(name).startsWith(ChatColor.stripColor(displayName)))
				return true;
		}
		return false;
	}

	public boolean isSecondary() {
		return secondary;
	}

	public Material getCurrentMaterial() {
		return materials.get(currentSelection);
	}

	public ToggleableItem setSoupItem(boolean soupItem) {
		this.soupItem = soupItem;
		return this;
	}

	public boolean isSoupItem() {
		return soupItem;
	}

	public ArrayList<String> getLore() {
		ArrayList<String> str = new ArrayList<String>();
		str.add(lore);
		str.add("");
		str.add("§6§lLeft-Click");
		str.add("§9  " + displayName + ":");
		for (int i = 0; i < types.size(); i += 1) {

			if (currentSelection == i) {
				str.add("§a  ► " + ChatColor.stripColor(types.get(i)).trim());
			} else
				str.add("§c    " + ChatColor.stripColor(types.get(i)).trim());
		}
		if (secondary) {
			str.add("");
			str.add("§6§lRight-Click");
			str.add("§b  " + secondaryName + ":");
			for (int i = 0; i < secondaries.size(); i += 1) {
				if (currentSelectionSecondary == i) {
					String secondary = "";
					if (secondaries.get(i) == 0)
						secondary = "No " + secondaryItemName;
					else
						secondary = secondaryItemName + " " + RomanNumeral.convertToRoman(secondaries.get(i));
					str.add("§a  ► " + secondary);
				} else if (secondaries.get(i) == 0)
					str.add("§c    " + "No " + secondaryItemName);

				else
					str.add("§c    " + secondaryItemName + " " + RomanNumeral.convertToRoman(secondaries.get(i)));
			}
		}
		return str;
	}

	public ArrayList<Material> getMaterials() {
		return materials;
	}

	public void next(ItemStack item, int slot, Player who) {
		if (item.hasItemMeta()) {
			if (isSoupItem()) {
				ItemStack soup = who.getOpenInventory().getItem(MatchRequest.HEAL_SLOT);

				if (soup.getType() == Material.POTION) {
					materials = new ArrayList<Material>() {
						private static final long serialVersionUID = 1L;
						{
							add(Material.GLASS_BOTTLE);
							add(Material.POTION);
						}
					};
				} else if (soup.getType() == Material.MUSHROOM_SOUP) {
					materials = new ArrayList<Material>() {
						private static final long serialVersionUID = 1L;
						{
							add(Material.BOWL);
							add(Material.MUSHROOM_SOUP);
						}
					};

				}

			}
			currentSelection += 1;
			if (currentSelection == types.size())
				currentSelection = 0;
			if (isPotionHeal()) {
				ItemStack soup = who.getOpenInventory().getItem(MatchRequest.SOUP_SLOT);
				if (getCurrentMaterial() == Material.POTION) {
					if (soup.getType() == Material.BOWL || soup.getType() == Material.GLASS_BOTTLE) {
						soup.setType(Material.GLASS_BOTTLE);
					} else
						soup.setType(Material.POTION);
				} else if (getCurrentMaterial() == Material.MUSHROOM_SOUP) {
					if (soup.getType() == Material.MUSHROOM_SOUP || soup.getType() == Material.POTION) {
						soup.setType(Material.MUSHROOM_SOUP);
					} else
						soup.setType(Material.BOWL);

				}
			}
			item.setDurability((short) (int) data.get((currentSelection >= data.size() ? data.size() - 1 : currentSelection)));
			item.setType(getMaterials().get(currentSelection));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(displayName);
			meta.setLore(getLore());
			item.setItemMeta(meta);
			if (item.getType() != Material.GLASS_BOTTLE) {
				Attributes attr = new Attributes(item);
				attr.clear();
				ItemStack stack = attr.getStack();
				who.getOpenInventory().setItem(slot, stack);

			} else {
				who.getOpenInventory().setItem(slot, item);

			}
		}
	}

	public void nextSecondary(ItemStack item, int slot, Player who) {
		ItemMeta meta = item.getItemMeta();
		currentSelectionSecondary += 1;
		if (currentSelectionSecondary == secondaries.size())
			currentSelectionSecondary = 0;
		meta.setLore(getLore());
		item.setItemMeta(meta);
		if (enchant != null)
			if (!(currentSelectionSecondary == 0 && ignoreFirst))
				item.addUnsafeEnchantment(enchant, currentSelectionSecondary + (ignoreFirst ? 0 : 1));
			else
				item.removeEnchantment(enchant);
		Attributes attr = new Attributes(item);
		attr.clear();
		who.getOpenInventory().setItem(slot, attr.getStack());
	}
}