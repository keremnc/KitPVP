package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public abstract class MatchRequest implements Listener {
	public final static int POTION_SLOT = 16;
	public final static int SOUP_SLOT = 14;
	public final static int CHESTPLATE_SLOT = 12;
	public final static int SWORD_SLOT = 10;

	private Player sender;
	private String recipientName;

	public MatchRequest(Player sender, Player recipient) {
		this.sender = sender;
		this.recipientName = recipient.getName();
		sender.closeInventory();
		String title = "Challenging: " + recipient.getName();
		Inventory inv = Bukkit.createInventory(sender, 45, title);
		createBaseInventory(inv);
		sender.openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, KitPVP.get());
	}

	public void createBaseInventory(Inventory inv) {
		for (int i = 0; i < 45; i += 1) {
			inv.setItem(i, Utilities.generateItem(Material.PISTON_EXTENSION, " "));
		}
		int start = 19;
		for (Loadout kit : Loadout.getLoadouts().subList(0, 3)) {
			ItemStack item = kit.getIcon();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§a" + kit.getName());
			ArrayList<String> lores = new ArrayList<String>();
			lores.addAll(wrap(kit.getDescription()));
			meta.setLore(lores);
			item.setItemMeta(meta);
			item.setAmount(1);
			Attributes att = new Attributes(item);
			att.clear();
			inv.setItem(start, att.getStack());
			start += 2;
		}
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		ArrayList<String> stats = new ArrayList<String>();
		Stat s = KitAPI.getStatManager().getStat(recipientName);
		if (s != null) {
			for (StatObjective sb : StatObjective.values()) {
				if (sb.isDisplay())
					stats.add("§6" + sb.getFriendlyName() + ":§f " + s.get(sb));
			}
		}
		meta.setLore(stats);
		meta.setDisplayName("§a§l" + recipientName);
		head.setItemMeta(meta);
		inv.setItem(4, head);
		inv.setItem(25, Core.get().generateItem(Material.ANVIL, 0, "§6§lCustom Match", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add("§9Challenge your opponent to a custom match!");
			}
		}));
	}

	public void createCustomInventory(Inventory inv) {
		for (int i = 0; i < 45; i += 1) {
			inv.setItem(i, Utilities.generateItem(Material.PISTON_EXTENSION, " "));
		}
		inv.setItem(SWORD_SLOT, Core.get().generateItem(Material.DIAMOND_SWORD, 0, "§bSword§l§a      Diamond", new String[] { "§9Click to switch sword types." }));
		inv.setItem(CHESTPLATE_SLOT, Core.get().generateItem(Material.IRON_CHESTPLATE, 0, "§5Chestplate§l§a      Iron", new String[] { "§9Click to toggle refilling." }));
		inv.setItem(SOUP_SLOT, Core.get().generateItem(Material.BOWL, 0, "§bRefilling§l§c      Disabled", new String[] { "§9Click to switch armor types." }));
		inv.setItem(POTION_SLOT, Core.get().generateItem(Material.BOW, 0, "§bPotions§l§a      None", new String[] { "§9Click to toggle potions." }));
		ItemStack head = new ItemStack(Material.SKULL_ITEM);
		head.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		ArrayList<String> stats = new ArrayList<String>();
		Stat s = KitAPI.getStatManager().getStat(recipientName);
		if (s != null) {
			for (StatObjective sb : StatObjective.values()) {
				if (sb.isDisplay())
					stats.add("§6" + sb.getFriendlyName() + ":§f " + s.get(sb));
			}
		}
		meta.setLore(stats);
		meta.setDisplayName("§a§l" + recipientName);
		head.setItemMeta(meta);
		inv.setItem(4, head);
		for (int i : new int[] { 32, 31, 30 }) {
			inv.setItem(i, Core.get().generateItem(Material.WOOL, 5, "§a§lCreate the Loadout!", new ArrayList<String>() {
				private static final long serialVersionUID = 1L;

				{
					add("§9Click here to send the 1v1 request.");
				}
			}));
		}

	}

	private List<String> wrap(String string) {
		String[] split = string.split(" ");
		string = "";
		ChatColor color = ChatColor.BLUE;
		ArrayList<String> newString = new ArrayList<String>();
		for (int i = 0; i < split.length; i++) {
			if (string.length() > 26 || string.endsWith(".") || string.endsWith("!")) {
				newString.add(color + string);
				if (string.endsWith(".") || string.endsWith("!"))
					newString.add("");
				string = "";
			}
			string += (string.length() == 0 ? "" : " ") + split[i];
		}
		newString.add(color + string);
		return newString;
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player who = (Player) e.getPlayer();
		if (who.getName().equalsIgnoreCase(sender.getName())) {
			HandlerList.unregisterAll(this);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player who = (Player) e.getWhoClicked();
		if (e.getInventory() != null && e.getInventory().getTitle().startsWith("Challenging"))
			if (who.getName().equalsIgnoreCase(sender.getName())) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
						String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
						Loadout l = Loadout.getByName(name);
						if (l != null) {
							onSelect(l);
							HandlerList.unregisterAll(this);
							e.getWhoClicked().closeInventory();
						}
						if (item.getType() == Material.ANVIL) {
							Inventory inv = e.getInventory();
							createCustomInventory(inv);
						}

					}
				}
			}
	}

	public abstract void onSelect(Loadout loadout);

}
