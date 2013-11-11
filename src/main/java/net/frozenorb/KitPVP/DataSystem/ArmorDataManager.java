package net.frozenorb.KitPVP.DataSystem;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.KitSystem.ArmorPrice.ArmorItem;
import net.frozenorb.KitPVP.KitSystem.ArmorPrice.ArmorType;

public class ArmorDataManager extends DataManager {
	private static HashSet<ArmorItem> items = new HashSet<ArmorItem>();

	public ArmorDataManager(File f) {
		super(f);
	}

	@Override
	public void onLoad() {
		if (getData().containsField(ArmorType.ITEM.getName())) {
			BasicDBObject obj = (BasicDBObject) getData().get(ArmorType.ITEM.getName());
			for (Entry<String, Object> entry : obj.entrySet()) {
				int price = (int) entry.getValue();
				ArmorItem item = new ArmorItem(entry.getKey(), price, ArmorType.ITEM);
				items.add(item);

			}
		}
		if (getData().containsField(ArmorType.POTION.getName())) {
			BasicDBObject obj = (BasicDBObject) getData().get(ArmorType.POTION.getName());
			for (Entry<String, Object> entry : obj.entrySet()) {
				int price = (int) entry.getValue();
				ArmorItem item = new ArmorItem(entry.getKey(), price, ArmorType.POTION);
				items.add(item);
			}
		}
		if (getData().containsField(ArmorType.ENCHANTMENT.getName())) {
			BasicDBObject obj = (BasicDBObject) getData().get(ArmorType.ENCHANTMENT.getName());
			for (Entry<String, Object> entry : obj.entrySet()) {
				int price = (int) entry.getValue();
				ArmorItem item = new ArmorItem(entry.getKey(), price, ArmorType.ENCHANTMENT);
				items.add(item);
			}
		}
	}

	public BasicDBObject toBasicDBObject() {
		BasicDBObject base = new BasicDBObject();
		BasicDBObject itemTypes = new BasicDBObject();
		BasicDBObject potTypes = new BasicDBObject();
		BasicDBObject enchantTypes = new BasicDBObject();

		for (ArmorItem tem : items) {
			if (tem.getType() == ArmorType.ITEM) {
				itemTypes.append(tem.getName(), tem.getPrice());
			}
			if (tem.getType() == ArmorType.POTION) {
				potTypes.append(tem.getName(), tem.getPrice());
			}
			if (tem.getType() == ArmorType.ENCHANTMENT) {
				enchantTypes.append(tem.getName(), tem.getPrice());
			}
		}
		base.append(ArmorType.ITEM.getName(), itemTypes).append(ArmorType.POTION.getName(), potTypes).append(ArmorType.ENCHANTMENT.getName(), enchantTypes);
		return base;
	}

	public void setPrice(ItemStack item, int price) {
		String name = getName(item);
		Iterator<ArmorItem> iter = items.iterator();
		while (iter.hasNext())
			if (iter.next().getName().equalsIgnoreCase(name))
				iter.remove();
		items.add(new ArmorItem(name, price, ArmorType.ITEM));
		setData(toBasicDBObject());
		saveData();
	}

	public void setPrice(PotionEffect item, int price) {
		String name = getName(item);
		Iterator<ArmorItem> iter = items.iterator();
		while (iter.hasNext())
			if (iter.next().getName().equalsIgnoreCase(name))
				iter.remove();
		items.add(new ArmorItem(name, price, ArmorType.POTION));
		setData(toBasicDBObject());
		saveData();

	}

	public void setPrice(Enchantment item, int level, int price) {
		String name = item.getName().toUpperCase() + ":" + level;
		Iterator<ArmorItem> iter = items.iterator();
		while (iter.hasNext())
			if (iter.next().getName().equalsIgnoreCase(name))
				iter.remove();
		items.add(new ArmorItem(name, price, ArmorType.ENCHANTMENT));
		setData(toBasicDBObject());
		saveData();

	}

	public String getName(PotionEffect pot) {
		return pot.getType().getName().toUpperCase() + ":" + pot.getAmplifier();
	}

	public String getName(ItemStack item) {
		return item.getType().toString().toUpperCase() + ":" + (item.getType().getMaxDurability() != 0 ? "-1" : "" + item.getDurability());
	}

	public int getPrice(String object) {
		for (ArmorItem item : items) {
			if (item.getName().equalsIgnoreCase(object)) {
				return item.getPrice();
			}
		}
		return 0;
	}

	public int getPrice(ItemStack item) {
		String name = getName(item);
		int base = getPrice(name);
		for (Entry<Enchantment, Integer> en : item.getEnchantments().entrySet()) {
			base += getPrice(en.getKey().getName().toUpperCase() + ":" + en.getValue());
		}
		return base;
	}

	public int getTotalPrice(Player p) {
		int base = 0;
		for (ItemStack item : p.getInventory().getContents())
			if (item != null)
				base += getPrice(item);
		for (ItemStack item : p.getInventory().getArmorContents())
			if (item != null)
				base += getPrice(item);
		for (PotionEffect pot : p.getActivePotionEffects())
			base += getPrice(getName(pot));
		return base;
	}

}
