package net.frozenorb.KitPVP.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Non initializable, final class used to quickly call static methods from around the plugin
 * 
 * @author Kerem Celik
 * 
 */
public final class Utilities {
	private Utilities() {
	}

	public static ItemStack generateLeatherArmor(Material item, org.bukkit.Color c) {
		ItemStack sm = new ItemStack(item);

		LeatherArmorMeta m = (LeatherArmorMeta) sm.getItemMeta();
		m.setColor(c);
		sm.setItemMeta(m);
		return sm;
	}

	public static ItemStack generateLeatherArmor(Material item, org.bukkit.Color c, int i) {
		ItemStack sm = new ItemStack(item);
		LeatherArmorMeta m = (LeatherArmorMeta) sm.getItemMeta();
		m.setColor(c);
		sm.setItemMeta(m);
		if (i != 0)
			sm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, i);
		return sm;
	}

	public static String readFile(File f) {
		StringBuilder dr = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				dr.append(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return dr.toString();
	}

	public static ItemStack generateItem(Material m, String name) {
		ItemStack i = new ItemStack(m);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		i.setItemMeta(meta);
		return i;
	}

	public static ItemStack generateItem(Material m, Enchantment e, int value) {
		ItemStack i = new ItemStack(m);
		i.addUnsafeEnchantment(e, value);
		return i;
	}

	public static ItemStack generateItem(Material m, Enchantment e, int value, Enchantment e2, int value2, String name) {
		ItemStack i = new ItemStack(m);
		i.addUnsafeEnchantment(e, value);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		i.setItemMeta(meta);
		i.addUnsafeEnchantment(e2, value2);
		return i;
	}

	public static ItemStack generateItem(Material m, Enchantment e, int value, Enchantment e2, int value2) {
		ItemStack i = new ItemStack(m);
		i.addUnsafeEnchantment(e, value);
		i.addUnsafeEnchantment(e2, value2);
		return i;
	}

	public static ItemStack generateItem(Material m) {
		ItemStack i = new ItemStack(m);
		return i;
	}

	public static ItemStack[] getFullSet(Material mat, Enchantment e, int value) {
		String matBase = mat.toString().split("_")[0];
		ItemStack[] fullSet = new ItemStack[4];
		fullSet[0] = generateItem(Material.valueOf(matBase + "_BOOTS"), e, value);
		fullSet[1] = generateItem(Material.valueOf(matBase + "_LEGGINGS"), e, value);
		fullSet[2] = generateItem(Material.valueOf(matBase + "_CHESTPLATE"), e, value);
		fullSet[3] = generateItem(Material.valueOf(matBase + "_HELMET"), e, value);
		return fullSet;
	}

	public static ItemStack[] getFullSet(Material mat, Enchantment e, int value, Enchantment e2, int value2) {
		String matBase = mat.toString().split("_")[0];
		ItemStack[] fullSet = new ItemStack[4];
		fullSet[0] = generateItem(Material.valueOf(matBase + "_BOOTS"), e, value, e2, value2);
		fullSet[1] = generateItem(Material.valueOf(matBase + "_LEGGINGS"), e, value, e2, value2);
		fullSet[2] = generateItem(Material.valueOf(matBase + "_CHESTPLATE"), e, value, e2, value2);
		fullSet[3] = generateItem(Material.valueOf(matBase + "_HELMET"), e, value, e2, value2);
		return fullSet;
	}

	public static ItemStack[] getFullSet(Material mat) {
		String matBase = mat.toString().split("_")[0];
		ItemStack[] fullSet = new ItemStack[4];
		fullSet[0] = generateItem(Material.valueOf(matBase + "_BOOTS"));
		fullSet[1] = generateItem(Material.valueOf(matBase + "_LEGGINGS"));
		fullSet[2] = generateItem(Material.valueOf(matBase + "_CHESTPLATE"));
		fullSet[3] = generateItem(Material.valueOf(matBase + "_HELMET"));
		return fullSet;
	}

}
