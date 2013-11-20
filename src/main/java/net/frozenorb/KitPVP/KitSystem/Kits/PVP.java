package net.frozenorb.KitPVP.KitSystem.Kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.frozenorb.KitPVP.KitSystem.BaseKit;
import net.frozenorb.KitPVP.Utilities.Utilities;

public class PVP extends BaseKit {


	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		inv.setArmorContents(Utilities.getFullSet(Material.IRON_SWORD));
		inv.setItem(0, Utilities.generateItem(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 1));
		return inv;
	}

	@Override
	public Listener getListener() {
		return null;
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] { new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0) };
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public String getDescription() {
		return "The standard PVP kit.";
	}

	@Override
	public Material getIconMaterial() {
		return Material.DIAMOND_SWORD;
	}

	@Override
	public boolean hasKit(Player p) {
		return true;
	}
}
