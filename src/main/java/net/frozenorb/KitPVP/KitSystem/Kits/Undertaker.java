package net.frozenorb.KitPVP.KitSystem.Kits;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.BaseKit;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.Utilities.Utilities;

public class Undertaker extends BaseKit {
	private HashMap<String, BukkitRunnable> tasks = new HashMap<String, BukkitRunnable>();

	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		inv.setArmorContents(Utilities.getFullSet(Material.CHAINMAIL_BOOTS));
		inv.setHelmet(Utilities.generateItem(Material.DIAMOND_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 4));
		inv.setBoots(Utilities.generateItem(Material.IRON_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 4));
		inv.setItem(0, Utilities.generateItem(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 3));
		inv.setItem(1, Utilities.generateItem(Material.EMERALD, "§9§lUndertaker Gem"));

		return inv;
	}

	@Override
	public Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onPlayerDeath(PlayerDeathEvent e) {
				if (tasks.containsKey(e.getEntity().getName())) {
					tasks.get(e.getEntity().getName()).cancel();
					tasks.remove(e.getEntity().getName());
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent e) {
				if (tasks.containsKey(e.getPlayer().getName())) {
					tasks.get(e.getPlayer().getName()).cancel();
					tasks.remove(e.getPlayer().getName());
				}
			}

			@EventHandler
			public void onEntityInteract(PlayerInteractEntityEvent e) {
				if (KitAPI.getKitManager().getKitsOnPlayers().containsKey(e.getPlayer().getName()) && KitAPI.getKitManager().getKitsOnPlayers().get(e.getPlayer().getName()).getName().equals(getName()))
					if (!KitAPI.getRegionChecker().isRegion(Region.DUEL_SPAWN, e.getPlayer().getLocation()) && e.getRightClicked() instanceof Player && e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() == Material.EMERALD) {
						final Player p = (Player) e.getRightClicked();
						if (KitAPI.getPlayerManager().hasSpawnProtection(p)) {
							e.setCancelled(true);
							e.getPlayer().sendMessage(ChatColor.RED + "That player has spawn protection.");
							return;
						}
						if (p.getInventory().getChestplate() != null) {
							final ItemStack chest = p.getInventory().getChestplate();
							if (!KitAPI.getKitManager().canUseAbility(e.getPlayer(), KitAPI.getKitManager().getByName(getName()))) {
								e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + getCooldown(e.getPlayer()) + " seconds.");
								return;
							}
							KitAPI.getStatManager().getLocalData(e.getPlayer().getName()).getPlayerKitData().get(KitAPI.getKitManager().getByName(getName())).incrementAbility(1);
							KitAPI.getKitManager().useAbility(e.getPlayer(), KitAPI.getKitManager().getByName(getName()), 15000);
							p.getInventory().setChestplate(null);
							p.playSound(p.getLocation(), Sound.WITHER_DEATH, 20F, 0.1F);
							e.getPlayer().playSound(p.getLocation(), Sound.WITHER_DEATH, 20F, 0.1F);
							e.getPlayer().sendMessage(ChatColor.GREEN + "You have stripped " + p.getName() + " of their chestplate!");
							p.sendMessage(ChatColor.RED + "Your chestplate was removed by an Undertaker!");
							BukkitRunnable run = new BukkitRunnable() {

								@Override
								public void run() {
									if (p.isOnline())
										p.getInventory().setChestplate(chest);
								}
							};
							tasks.put(p.getName(), run);
							run.runTaskLater(KitPVP.get(), 100L);
							return;
						}
						e.getPlayer().sendMessage(ChatColor.GOLD + p.getName() + " doesn't have a chestplate to remove!");
					}
			}
		};
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] { new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0) };
	}

	@Override
	public int getId() {
		return 3;
	}

	@Override
	public String getDescription() {
		return "Right click on a player to remove their chestplate for 5 seconds! Extremely useful for taking out the guys in diamond armor.";
	}

	@Override
	public Material getIconMaterial() {
		return Material.LEATHER_CHESTPLATE;
	}

	@Override
	public String getMetaName() {
		return "Chestplates stolen";
	}

}
