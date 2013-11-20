package net.frozenorb.KitPVP.Server;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Pagination.KitInventory;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.KitPVP.Reflection.CommandManager;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;

@SuppressWarnings("deprecation")
public class ServerManager {

	private KitPVP plugin;
	private HashSet<String> clearOnLogout = new HashSet<String>();
	private HashSet<String> warpToMatch = new HashSet<String>();
	private HashSet<String> ignoreTeleport = new HashSet<String>();

	public ServerManager(KitPVP plugin) {
		this.plugin = plugin;

	}

	public HashSet<String> getIgnoreTeleport() {
		return ignoreTeleport;
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
		KitAPI.getKitManager().getKitsOnPlayers().remove(p.getName());
		p.setHealth(20D);
		p.setVelocity(new Vector(0, 0, 0));
		p.setFireTicks(1);

		if (KitAPI.getRegionChecker().isRegion(Region.EARLY_HG, p.getLocation()))
			return;
		if (warpToMatch.contains(p.getName())) {
			KitAPI.getKitPVP().getCommandManager().teleport(p, CommandManager.DUEL_LOCATION);
			return;
		}
		if (KitAPI.getMatchManager().isInMatch(p.getName()) && KitAPI.getMatchManager().getCurrentMatches().get(p.getName()).isInProgress())
			return;
		if (ignoreTeleport.contains(p.getName())) {
			ignoreTeleport.remove(p.getName());
		} else
			p.teleport(getSpawn());

		KitAPI.getPlayerManager().getSpawnProtection().add(p.getName());
		Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

			@Override
			public void run() {
				if (warpToMatch.contains(p.getName()))
					return;
				if (KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, p.getLocation()))
					return;
				addSpawnItems(p);

			}
		}, 5L);
	}

	public void addSpawnItems(Player p) {
		GamerProfile prof = KitAPI.getPlayerManager().getProfile(p.getName().toLowerCase());
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
		ItemStack feather = new ItemStack(Material.FEATHER);
		ItemMeta fm = feather.getItemMeta();
		fm.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Warp to the 1v1 Arena");
		ArrayList<String> fl = new ArrayList<String>();
		fl.add("§9Right click this to warp the the 1v1 Arena!");
		fm.setLore(fl);
		feather.setItemMeta(fm);
		p.getInventory().setItem(8, feather);

		p.updateInventory();
		p.getInventory().setHeldItemSlot(0);
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

	public void setVisible(Player player, boolean visible) {
		if (!visible) {
			final byte b = Byte.parseByte("0110000", 2);
			((CraftPlayer) player).getHandle().getDataWatcher().watch(0, b);
		} else {
			final byte b = Byte.parseByte("0000000", 2);
			((CraftPlayer) player).getHandle().getDataWatcher().watch(0, b);

		}

	}

	public void applyHGInventory(Player p) {
		Core.get().clearPlayer(p);
		for (int i = 19; i < 26; i += 1) {
			p.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
		}
		p.getInventory().setItem(0, Utilities.generateItem(Material.STONE_SWORD, org.bukkit.enchantments.Enchantment.DURABILITY, 10));
		KitAPI.getPlayerManager().fillSoup(p.getInventory());
		for (int i = 9; i < 36; i += 1) {
			if (p.getInventory().getItem(i) == null)
				p.getInventory().setItem(i, Utilities.generateItem(Material.PISTON_EXTENSION, "§c§lUnusable Slot"));
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
