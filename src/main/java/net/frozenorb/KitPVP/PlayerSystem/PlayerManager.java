package net.frozenorb.KitPVP.PlayerSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.RegionSysten.Region;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerManager {
	private HashMap<String, GamerProfile> gamerProfiles = new HashMap<String, GamerProfile>();
	private HashSet<String> spawnProtetcion = new HashSet<String>();

	public HashSet<String> getSpawnProtection() {
		return spawnProtetcion;
	}

	public boolean hasSpawnProtection(Player p) {
		return spawnProtetcion.contains(p.getName());
	}

	public void clearInventory(Player p) {
		for (PotionEffect pot : p.getActivePotionEffects())
			p.removePotionEffect(pot.getType());
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setArmorContents(null);
		p.setMaxHealth(20D);
		p.setHealth(20D);
		p.setFoodLevel(20);
		p.setLevel(0);
		p.setFireTicks(0);
		p.setExp(0.0F);
	}

	public boolean canWarp(Player player) {
		int max = 31;
		if (hasSpawnProtection(player)) {
			return true;
		}
		if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, player.getLocation()))
			return true;
		if (player.isOp()) {
			return true;
		}
		List<Entity> nearbyEntities = player.getNearbyEntities(max, max, max);
		for (Entity e : nearbyEntities) {
			if ((e instanceof Player)) {
				return false;
			}
		}

		return true;
	}

	public void clearInventory(PlayerInventory inv) {
		inv.clear();
		inv.setArmorContents(null);
	}

	public void fillSoup(PlayerInventory inv) {
		for (int i = 0; i < 8; i += 1) {
			inv.addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
	}

	public void registerProfile(String name, GamerProfile profile) {
		gamerProfiles.put(name.toLowerCase(), profile);
	}

	public GamerProfile getProfile(String str) {
		return gamerProfiles.get(str.toLowerCase());
	}

}
