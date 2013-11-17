package net.frozenorb.KitPVP.MatchSystem.Loadouts;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Utilities.Utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuffedSpeedLoadout extends Loadout {

	@Override
	public String getDescription() {
		return "Standard with Strength II and Speed II";
	}

	@Override
	public String getName() {
		return "Buffed w/ Speed II";
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] { new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1) };
	}

	@Override
	public PlayerInventory applyInventory(PlayerInventory inventory) {
		KitAPI.getPlayerManager().clearInventory(inventory);
		inventory.setArmorContents(Utilities.getFullSet(Material.IRON_HELMET));
		inventory.setItem(0, Utilities.generateItem(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 1));
		KitAPI.getPlayerManager().fillSoup(inventory);
		return inventory;
	}

	@Override
	public int getWeight() {
		return 2;
	}

}
