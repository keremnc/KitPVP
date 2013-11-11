package net.frozenorb.KitPVP.MatchSystem.MatchTypes;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Utilities.Utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class StandardLoadout extends Loadout {

	@Override
	public String getName() {
		return "Standard";
	}

	@Override
	public String getDescription() {
		return "Standard Match Loadout";
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] {};
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
		return 0;
	}
}
