package net.frozenorb.KitPVP.KitSystem;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
import net.frozenorb.KitPVP.RegionSysten.RegionMeta;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public final class KitFactory {
	KitFactory() {
	}

	public static SerializableKit createKit(final String name, final String permission, final String description, final ItemStack[] armor, final ItemStack[] items, final PotionEffect[] pots, final Material mat) {
		final SerializableKit skit = new SerializableKit() {

			@Override
			public PlayerInventory transformInventory(PlayerInventory inv) {
				inv.setArmorContents(armor);
				inv.setContents(items);
				return inv;
			}

			@Override
			public boolean hasKit(Player p) {
				return Core.get().hasPermission(p, getPermission());
			}

			@Override
			public boolean hasAbilityMeta() {
				return false;
			}

			@Override
			public PotionEffect[] getPotionEffects() {
				return pots;
			}

			@Override
			public String getPermission() {
				return permission;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getMetaName() {
				return null;
			}

			@Override
			public Listener getListener() {
				return null;
			}

			@Override
			public ItemStack getKitIcon() {
				return new ItemStack(mat);
			}

			@Override
			public int getId() {
				return KitPVP.getKits().size() + 1;
			}

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public void applyKit(Player p) {
				Core.get().clearPlayer(p);
				for (PotionEffect pe : getPotionEffects()) {
					p.addPotionEffect(pe);
				}
				p.getInventory().setContents(transformInventory(p.getInventory()).getContents());
				p.getInventory().setArmorContents(transformInventory(p.getInventory()).getArmorContents());
			}

			@Override
			public ItemStack[] getArmorContents() {
				return armor;
			}

			@Override
			public ItemStack[] getInventoryContents() {
				return items;
			}

			@Override
			public void commandRun(Player p) {
				if (KitAPI.getKitManager().hasKitOn(p.getName()) && !KitAPI.getPlayerManager().hasSpawnProtection(p)) {
					p.sendMessage(ChatColor.RED + "You may only use one kit per life!");
					return;
				}
				if (KitAPI.getRegionChecker().getRegion(p.getLocation()) != null) {
					RegionMeta meta = KitAPI.getRegionChecker().getRegion(p.getLocation()).getMeta();

					if (meta.getBlockedKits() != null) {
						for (Class<? super Kit> clazz : meta.getBlockedKits()) {
							Kit kit = KitAPI.getKitManager().getByName(clazz.getSimpleName());
							if (kit.getName().equalsIgnoreCase(getName())) {
								p.sendMessage(ChatColor.RED + "That kit is blocked in this region!");
								return;
							}
						}
					}
					if (meta.getUsableKits() != null) {
						if (meta.getUsableKits().length == 0) {
							p.sendMessage(ChatColor.RED + "You are not able to use kits in this region!");
							return;
						}
						label: {
							for (Class<? super Kit> clazz : meta.getUsableKits()) {
								Kit kit = KitAPI.getKitManager().getByName(clazz.getSimpleName());
								if (kit.getName().equalsIgnoreCase(getName())) {
									break label;
								}
							}
							p.sendMessage(ChatColor.RED + "That kit is not allowed in this region.");
							return;
						}
					}
				}
				if (Core.get().hasPermission(p, getPermission()) || p.hasPermission(getPermission())) {
					PlayerKitSelectEvent e = new PlayerKitSelectEvent(p, this);
					Bukkit.getPluginManager().callEvent(e);
					if (e.isCancelled())
						return;
					applyKit(p);
					p.sendMessage("§6You have chosen the kit §a" + getName() + "§6.");
				} else {
					p.sendMessage(ChatColor.RED + "You do not have access to that kit!");
				}

			}

		};
		return skit;
	}
}
