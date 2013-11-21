package net.frozenorb.KitPVP.MatchSystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.ItemSystem.ToggleableItem;
import net.frozenorb.KitPVP.MatchSystem.Loadouts.Loadout;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mongodb.BasicDBObject;

public abstract class MatchRequest implements Listener {
	/* Slot fields */
	private final static int POTION_SLOT = 16;
	private final static int SOUP_SLOT = 14;
	private final static int CHESTPLATE_SLOT = 12;
	private final static int SWORD_SLOT = 10;

	/* Player and recipient fields */
	private Player sender;
	private String recipientName;

	/*
	 * ToggleableItems - Not static due to the fact that we have individual types for each player
	 */
	private ToggleableItem soup = null;
	private ToggleableItem armor = null;
	private ToggleableItem sword = null;
	private ToggleableItem potions = null;

	public MatchRequest(Player sender, Player recipient) {
		soup = new ToggleableItem("§bRefilling", "§9Click to toggle refilling.", Material.BOWL, Material.MUSHROOM_SOUP, false);
		potions = new ToggleableItem("§bPotions", "§9Click to toggle potions.", new LinkedHashMap<String, Material>() {
			private static final long serialVersionUID = 1L;

			{
				put("§a§lNone", Material.GLASS_BOTTLE);
				put("§a§lStrength II", Material.POTION);
				put("§a§lSpeed II", Material.POTION);
				put("§a§lSpeed and Strength II", Material.POTION);
			}
		});
		sword = new ToggleableItem("§bSword", "§9Click to switch swords.", new LinkedHashMap<String, Material>() {
			private static final long serialVersionUID = 1L;

			{
				put("§a§lDiamond", Material.DIAMOND_SWORD);
				put("§a§lIron", Material.IRON_SWORD);
				put("§a§lStone", Material.STONE_SWORD);

			}
		}).setSecondary("Enchantments", new ArrayList<Integer>() {
			private static final long serialVersionUID = 1L;

			{
				for (int i = 0; i < 6; i++) {
					add(i);
				}
			}
		}, "Sharpness", Enchantment.DAMAGE_ALL);
		armor = new ToggleableItem("§bArmor", "§9Click to switch armor types.", new LinkedHashMap<String, Material>() {
			private static final long serialVersionUID = 1L;

			{
				put("§a§lIron", Material.IRON_CHESTPLATE);
				put("§a§lDiamond", Material.DIAMOND_CHESTPLATE);
				put("§a§lChainmail", Material.CHAINMAIL_CHESTPLATE);
				put("§a§lNone", Material.CAULDRON);

			}
		}).setSecondary("Enchantments", new ArrayList<Integer>() {
			private static final long serialVersionUID = 1L;

			{
				for (int i = 0; i < 5; i++) {
					add(i);
				}
			}
		}, "Protection", Enchantment.PROTECTION_ENVIRONMENTAL);
		this.sender = sender;
		this.recipientName = recipient.getName();
		sender.closeInventory();
		String title = "Challenging: " + recipient.getName();
		Inventory inv = Bukkit.createInventory(sender, 45, title);
		createBaseInventory(inv);
		sender.openInventory(inv);
		Bukkit.getPluginManager().registerEvents(this, KitPVP.get());
		KitAPI.getItemManager().registerItem(soup, sender);
		KitAPI.getItemManager().registerItem(sword, sender);
		KitAPI.getItemManager().registerItem(armor, sender);
		KitAPI.getItemManager().registerItem(potions, sender);

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

		inv.setItem(SWORD_SLOT, sword.initialize());
		inv.setItem(CHESTPLATE_SLOT, armor.initialize());
		inv.setItem(SOUP_SLOT, soup.initialize());
		inv.setItem(POTION_SLOT, potions.initialize());
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
			finish(who);

		}
	}

	private void finish(Player who) {
		KitAPI.getItemManager().unregisterPlayer(who);
		soup = null;
		potions = null;
		armor = null;
		sword = null;
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player who = (Player) e.getWhoClicked();
		if (e.getInventory() != null && e.getInventory().getTitle().startsWith("Challenging"))
			if (who.getName().equalsIgnoreCase(sender.getName())) {
				e.setCancelled(true);
				if (e.getCurrentItem() != null) {
					ItemStack item = e.getCurrentItem();
					if (item.getType() == Material.WOOL) {
						onSelect(createLoadout(armor.getCurrentPrimaryValue(), potions.getCurrentPrimaryValue(), soup.getCurrentPrimaryValue(), sword.getCurrentPrimaryValue(), armor.getCurrentSecondaryValue(), sword.getCurrentSecondaryValue()));
						finish(who);
						return;
					}

					if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
						if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT)
							KitAPI.getItemManager().handleRightClick(item, e.getRawSlot(), who);
						else
							KitAPI.getItemManager().handleLeftClick(item, e.getRawSlot(), who);
						String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
						Loadout l = Loadout.getByName(name);
						if (l != null) {
							onSelect(l);
							finish(who);
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

	/**
	 * Attempts to create the loadout using the given information
	 * 
	 * @return loadout
	 */
	public Loadout createLoadout(final String armor, final String potion, final String soup, final String sword, final int armorlevel, final int swordlevel) {
		final Loadout l = new Loadout() {

			@Override
			public int getWeight() {
				return 0;
			}

			@Override
			public PotionEffect[] getPotionEffects() {
				if (potion.equals("Strength II")) {
					return new PotionEffect[] { new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1) };

				}
				if (potion.equals("Speed II")) {
					return new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1) };

				}
				if (potion.equals("Speed and Strength II")) {
					return new PotionEffect[] { new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1) };

				}
				return new PotionEffect[] {};
			}

			@Override
			public String getName() {
				return "Custom";
			}

			@Override
			public String getDescription() {
				return new BasicDBObject("refills", soup.equalsIgnoreCase("enabled")).append("sword", sword).append("armor", armor).append("potions", potion).toString();
			}

			@Override
			public PlayerInventory applyInventory(PlayerInventory inventory) {
				KitAPI.getPlayerManager().clearInventory(inventory);
				if (!(armor.equalsIgnoreCase("none"))) {
					if (armorlevel > 0)

						inventory.setArmorContents(Utilities.getFullSet(Material.valueOf(armor.toUpperCase() + "_CHESTPLATE"), Enchantment.PROTECTION_ENVIRONMENTAL, armorlevel));
					else
						inventory.setArmorContents(Utilities.getFullSet(Material.valueOf(armor.toUpperCase() + "_CHESTPLATE")));

				}
				if (swordlevel > 0)
					inventory.setItem(0, Utilities.generateItem(Material.getMaterial(sword.toUpperCase() + "_SWORD"), Enchantment.DAMAGE_ALL, swordlevel));
				else
					inventory.setItem(0, Utilities.generateItem(Material.getMaterial(sword.toUpperCase() + "_SWORD")));
				if (soup.equalsIgnoreCase("enabled"))
					KitAPI.getPlayerManager().fillSoupCompletely(inventory);
				else
					KitAPI.getPlayerManager().fillSoup(inventory);

				return inventory;
			}
		};
		return l;
	}

	public abstract void onSelect(Loadout loadout);

}
