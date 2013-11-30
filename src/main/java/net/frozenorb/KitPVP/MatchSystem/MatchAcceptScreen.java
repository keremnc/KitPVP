package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.PlayerSystem.PlayerManager;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

public abstract class MatchAcceptScreen implements Listener {

	private Player clicker;
	private Match match;

	public MatchAcceptScreen(Player clicker, Match match) {
		this.clicker = clicker;
		this.match = match;
		clicker.closeInventory();
		Inventory inv = Bukkit.createInventory(null, 36, "Match found: " + match.getChallenger().getName());
		ItemStack accept = Core.get().generateItem(Material.WOOL, 5, "§a§lAccept Match", new String[] { "§9Click this to accept the match." });
		ItemStack decline = Core.get().generateItem(Material.WOOL, 14, "§c§lDecline Match", new String[] { "§9Click this to decline match." });
		ArrayList<String> lore = new ArrayList<String>();
		try {
			BasicDBObject mInfo = (BasicDBObject) JSON.parse(match.getType().getDescription());
			for (Entry<String, Object> entry : mInfo.entrySet()) {
				lore.add("§6" + entry.getKey() + ":§f " + entry.getValue());
			}
		} catch (JSONParseException ex) {
			lore.add(match.getType().getDescription());

		}
		ItemStack info = Core.get().generateItem(Material.MAP, 0, "§b§lLoadout Data", match.getMetadata(false, false));

		int[] acc = new int[] { 10, 11, 19, 20 };
		int[] dec = new int[] { 15, 16, 24, 25 };
		for (int i = 0; i < 36; i += 1) {
			inv.setItem(i, Utilities.generateItem(PlayerManager.UNUSABLE_SLOT, " "));
		}
		for (int i : acc) {
			inv.setItem(i, accept);
		}
		for (int i : dec) {
			inv.setItem(i, decline);
		}
		inv.setItem(22, info);
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		ArrayList<String> stats = new ArrayList<String>();
		Stat s = KitAPI.getStatManager().getStat(match.getChallenger().getName());
		if (s != null) {
			for (StatObjective sb : StatObjective.values()) {
				if (sb.isDisplay())
					stats.add("§6" + sb.getFriendlyName() + ":§f " + s.get(sb));
			}
		}
		meta.setLore(stats);
		meta.setDisplayName("§a§l" + match.getChallenger().getName());
		head.setItemMeta(meta);
		inv.setItem(4, head);

		clicker.openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, KitPVP.get());
	}

	public Player getClicker() {
		return clicker;
	}

	public Match getMatch() {
		return match;
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player who = (Player) e.getPlayer();
		if (who.getName().equalsIgnoreCase(clicker.getName())) {
			HandlerList.unregisterAll(this);

		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player who = (Player) e.getWhoClicked();

		if (who.getName().equalsIgnoreCase(clicker.getName())) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.WOOL) {
				ItemStack item = e.getCurrentItem();
				if (item.getDurability() == 14) {
					if (isValid(match))
						onDecline(match);
					who.closeInventory();
					who.playSound(who.getLocation(), Sound.ARROW_HIT, 20F, 20F);
				} else if (item.getDurability() == 5) {
					if (isValid(match))
						onAccept(match);
					who.closeInventory();
					who.playSound(who.getLocation(), Sound.NOTE_PLING, 20F, 20F);
				}
				HandlerList.unregisterAll(this);
			}
		}
	}

	public boolean isValid(Match m) {
		if (m != null) {
			if (m.isInProgress())
				return false;
			if (m.getInvitedPlayer().equals(clicker.getName())) {
				return true;
			}
		}
		return false;
	}

	public abstract void onAccept(Match match);

	public abstract void onDecline(Match match);

}
