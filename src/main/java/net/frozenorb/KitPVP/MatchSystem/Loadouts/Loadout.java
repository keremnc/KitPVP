package net.frozenorb.KitPVP.MatchSystem.Loadouts;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Reflection.ClassGetter;
import net.frozenorb.Utilities.Core;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public abstract class Loadout {
	private static ArrayList<Loadout> matches = new ArrayList<Loadout>();

	public static void init() {
		for (Class<?> c : ClassGetter.getClassesForPackage(KitAPI.getKitPVP(), "net.frozenorb.KitPVP.MatchSystem.Loadouts")) {
			if (Loadout.class.isAssignableFrom(c)) {
				if (!Modifier.isAbstract(c.getModifiers())) {
					try {
						Loadout type = (Loadout) c.newInstance();
						matches.add(type);
						System.out.println("Loaded loadout: " + type.getName());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}

			}
		}
		Collections.sort(matches, new Comparator<Loadout>() {
			@Override
			public int compare(Loadout o1, Loadout o2) {
				return ((Integer) o1.getWeight()).compareTo(o2.getWeight());
			}
		});
	}

	/**
	 * Gets a list of matchtypes
	 * 
	 * @return matchtype
	 */
	public static ArrayList<Loadout> getLoadouts() {
		return matches;
	}

	public ItemStack getIcon() {
		return Core.get().generateItem(getMaterialIcon(), 0, getName(), new ArrayList<String>() {
			private static final long serialVersionUID = 1058366399386399139L;

			{
				add(getDescription());
			}
		});
	}

	public static Loadout getByName(String name) {
		for (Loadout t : matches)
			if (t.getName().equalsIgnoreCase(name))
				return t;
		return null;
	}

	/**
	 * Gets the Material used as the icon
	 * 
	 * @return icon
	 */
	public Material getMaterialIcon() {
		return Material.ENCHANTED_BOOK;
	}

	/**
	 * Gets the description
	 * 
	 * @return desc
	 */
	public abstract String getDescription();

	/**
	 * Gets the name of the MatchType
	 * 
	 * @return name
	 */
	public abstract String getName();

	/**
	 * Gets the PotionEffects to apply to the player
	 * 
	 * @return potion effects
	 */
	public abstract PotionEffect[] getPotionEffects();

	/**
	 * Applies the inventory to the player
	 * 
	 * @param inventory
	 *            player's inventory
	 * @return the applied inventory
	 */
	public abstract PlayerInventory applyInventory(PlayerInventory inventory);

	/**
	 * Called when the winner defeats the loser
	 * 
	 * @param winner
	 *            the winner of the match
	 * @param loser
	 *            the loser
	 */
	public void onDefeat(Player winner, Player loser) {
	}

	/**
	 * Gets the weighted value of the MatchType
	 * 
	 * @return weight
	 */
	public abstract int getWeight();

}
