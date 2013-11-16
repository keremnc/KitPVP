package net.frozenorb.KitPVP.Server;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.Pagination.KitInventory;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.Reflection.CommandManager;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;

public class ServerManager {

	private KitPVP plugin;
	private HashSet<String> clearOnLogout = new HashSet<String>();
	private HashSet<String> warpToMatch = new HashSet<String>();

	public ServerManager(KitPVP plugin) {
		this.plugin = plugin;
	}

	public void addClearOnLogout(String name) {
		clearOnLogout.add(name.toLowerCase());
	}

	public boolean isClearOnLogout(String name) {
		return clearOnLogout.contains(name.toLowerCase());
	}

	public HashSet<String> getWarpToMatch() {
		return warpToMatch;
	}

	public void removeLogout(String name) {
		clearOnLogout.remove(name.toLowerCase());
	}

	public KitPVP getPlugin() {
		return plugin;
	}

	/**
	 * Gets the Spawn location of the world
	 * 
	 * @return spawn
	 */
	public Location getSpawn() {
		return Bukkit.getWorld("world").getSpawnLocation().subtract(new Vector(0.5, -0.7, 0.5));
	}

	/**
	 * Opens the Kit Inventory for the player
	 * 
	 * @param p
	 *            player to open inventory for
	 */
	public void openKitInventory(Player p) {
		if (p.hasMetadata("KitInventory"))
			return;
		KitInventory inv = new KitInventory(p);
		inv.setKits();
		inv.openInventory();
	}

	public void handleRespawn(final Player p) {
		final GamerProfile prof = KitAPI.getPlayerManager().getProfile(p.getName().toLowerCase());
		KitAPI.getKitManager().getKitsOnPlayers().remove(p.getName());
		p.setHealth(20D);
		p.setVelocity(new Vector(0, 0, 0));
		p.setFireTicks(1);
		if (warpToMatch.contains(p.getName())) {
			KitAPI.getKitPVP().getCommandManager().teleport(p, CommandManager.DUEL_LOCATION);
			return;
		}
		if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, p.getLocation()))
			return;
		if (KitAPI.getMatchManager().isInMatch(p.getName()) && KitAPI.getMatchManager().getCurrentMatches().get(p.getName()).isInProgress())
			return;
		p.teleport(getSpawn());

		KitAPI.getPlayerManager().getSpawnProtection().add(p.getName());
		Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (warpToMatch.contains(p.getName()))
					return;
				if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, p.getLocation()))
					return;
				ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
				ItemMeta meta = book.getItemMeta();
				meta.setDisplayName(ChatColor.RED + "§lKits");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§9Open your kit menu.");
				meta.setLore(lore);
				book.setItemMeta(meta);
				p.getInventory().setItem(0, book);
				if (prof.getLastUsedKit() != null) {
					ItemStack books = new ItemStack(Material.WATCH);
					ItemMeta metas = books.getItemMeta();
					metas.setDisplayName(ChatColor.GREEN + "Last Kit: §e§l" + prof.getLastUsedKit().getName());
					ArrayList<String> lores = new ArrayList<String>();
					lores.add("§9Select your last used kit.");
					metas.setLore(lores);
					books.setItemMeta(metas);
					p.getInventory().setItem(1, books);
				}
				p.updateInventory();
				p.getInventory().setHeldItemSlot(0);
			}
		}, 5L);
	}

	/**
	 * Gets the amount of soups in the hotbar of the player
	 * 
	 * @param p
	 *            the player
	 * @return soup amount
	 */
	public int getSoupsInHotbar(Player p) {
		int soups = 0;
		for (int i = 0; i < 9; i += 1) {
			ItemStack item = p.getInventory().getItem(i);
			if (item != null) {
				if (item.getType() != null) {
					if (item.getType().equals(Material.MUSHROOM_SOUP)) {
						soups += 1;
					}
				}
			}
		}
		return soups;
	}

	public void applyHGInventory(Player p) {
		Core.get().clearPlayer(p);
		p.getInventory().setItem(0, Utilities.generateItem(Material.MUSHROOM_SOUP, org.bukkit.enchantments.Enchantment.DURABILITY, 10));
		KitAPI.getPlayerManager().fillSoup(p.getInventory());
		for (int i = 19; i < 26; i += 1) {
			p.getInventory().setItem(0, new ItemStack(Material.MUSHROOM_SOUP));
		}
	}

	/**
	 * Gets the amount of hearts the player has
	 * 
	 * @param p
	 *            the player
	 * @return hearts
	 */
	public double getHearts(Player p) {
		return Math.ceil((((Damageable) p).getHealth() / 2) * 2) / 2;
	}
}
