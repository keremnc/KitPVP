package net.frozenorb.KitPVP.PlayerSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.RegionSysten.Region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerManager {
	private HashMap<String, GamerProfile> gamerProfiles = new HashMap<String, GamerProfile>();
	private HashSet<String> spawnProtetcion = new HashSet<String>();

	/**
	 * Whether the player has spawn protection or not
	 * 
	 * @param p
	 *            the player to check
	 * @return whether the player has spawn protection
	 */
	public boolean hasSpawnProtection(Player p) {
		return spawnProtetcion.contains(p.getName());
	}

	/**
	 * Gives spawn protection to a player
	 * 
	 * @param p
	 *            the player to give spawn protection to
	 */
	public void giveSpawnProtection(Player p) {
		spawnProtetcion.add(p.getName());
	}

	/**
	 * Removes a player from the spawn protection set
	 * 
	 * @param p
	 *            the player to remove spawn protection from
	 */
	public void removeSpawnProtection(Player p) {
		spawnProtetcion.remove(p.getName());
	}

	/**
	 * Teleports a player to a location, while applying the necessary region metas
	 * 
	 * @param p
	 *            the player to teleport
	 * @param loc
	 *            the location to teleport to
	 */
	public void teleport(Player p, Location loc) {
		if (KitAPI.getRegionChecker().getRegion(loc) != null) {
			KitAPI.getRegionChecker().getRegion(loc).getMeta().onWarp(p);
		}
		p.teleport(loc);
		p.closeInventory();
	}

	/**
	 * Checks if the player has an empty inventory
	 * 
	 * @param p
	 *            the player to check
	 * @return if the inventory is empty or not
	 */
	public boolean isInventoryEmpty(Player p) {
		for (ItemStack item : p.getInventory().getContents()) {
			if (item != null)
				return false;
		}
		return true;
	}

	/**
	 * Clears a player's inventory, armor, exp, and resets health/foodlevel
	 * 
	 * @param p
	 *            the player to clear
	 */
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

	/**
	 * Gets whether the player is able to warp or not
	 * <p>
	 * Takes spawn protection, and nearby players into account
	 * 
	 * @param player
	 *            the player to check
	 * @return whether the player can warp instantly, or must wait
	 */
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

	/**
	 * Clears an inventory
	 * 
	 * @param inv
	 *            the inventory to clear
	 */
	public void clearInventory(PlayerInventory inv) {
		inv.clear();
		inv.setArmorContents(null);
	}

	/**
	 * Fills a player's hotbar with the given material
	 * 
	 * @param inv
	 *            the inventory to fill
	 * @param m
	 *            the material to fill with
	 */
	public void fillHotbar(PlayerInventory inv, Material m) {
		for (int i = 0; i < 8; i += 1) {
			if (m == Material.MUSHROOM_SOUP)
				inv.addItem(new ItemStack(m));
			else if (m == Material.POTION)
				inv.addItem(new ItemStack(m) {
					{
						setDurability((short) 16421);
					}
				});

		}
	}

	/**
	 * Fills a player's inventory with the given material
	 * 
	 * @param inv
	 *            the inventory to fill
	 * @param m
	 *            the material to fill with
	 */
	public void fillInventory(PlayerInventory inv, Material m) {
		for (int i = 0; i < 39; i += 1) {
			if (m == Material.MUSHROOM_SOUP)
				inv.addItem(new ItemStack(m));
			else if (m == Material.POTION)
				inv.addItem(new ItemStack(m) {
					{
						setDurability((short) 16421);
					}
				});

		}
	}

	/**
	 * Registers a {@link GamerProfile} object to a player
	 * 
	 * @param name
	 *            the name of the player
	 * @param profile
	 *            the profile to register
	 */
	public void registerProfile(String name, GamerProfile profile) {
		gamerProfiles.put(name.toLowerCase(), profile);
	}

	/**
	 * Gets the {@link GamerProfile} registered to a player
	 * 
	 * @param str
	 *            the name of the player
	 * @return the profile of the player, if existant
	 */
	public GamerProfile getProfile(String str) {
		return gamerProfiles.get(str.toLowerCase());
	}

}
