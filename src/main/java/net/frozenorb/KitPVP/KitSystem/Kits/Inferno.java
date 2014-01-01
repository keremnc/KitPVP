package net.frozenorb.KitPVP.KitSystem.Kits;

import java.util.HashMap;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.BaseKit;
import net.frozenorb.KitPVP.RegionSysten.Region;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Inferno extends BaseKit {
	private HashMap<Location, String> blockCombustors = new HashMap<Location, String>();

	private Block getHighestBlockWithin10Blocks(Block b) {
		int iterations = 0;
		Block bl = b;
		while (bl.getType() != Material.AIR && iterations <= 10) {
			bl = bl.getRelative(BlockFace.UP);
			iterations++;
		}
		return bl;
	}

	@Override
	public Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onPlayerInteract(PlayerInteractEvent e) {
				if (e.getPlayer() instanceof Player) {
					Player p = (Player) e.getPlayer();
					if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.BLAZE_POWDER) {
						if (hasKit(p)) {
							if (KitAPI.getRegionChecker().isRegion(Region.SPAWN, p.getLocation())) {
								p.sendMessage(ChatColor.RED + "You may not use this in spawn!");
								return;
							}
							if (!KitAPI.getKitManager().canUseAbility(e.getPlayer(), KitAPI.getKitManager().getByName(getName()))) {
								e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + getCooldown(e.getPlayer()) + " seconds.");
								return;
							}
							KitAPI.getStatManager().getPlayerData(e.getPlayer().getName()).getPlayerKitData().get(Inferno.this).incrementAbility(1);
							KitAPI.getKitManager().useAbility(e.getPlayer(), KitAPI.getKitManager().getByName(getName()), 15000);
							int index = 1;
							for (Location loc : Core.get().circle(e.getPlayer().getLocation(), 4, 2, false, true, 0)) {
								final Block b = getHighestBlockWithin10Blocks(loc.getBlock());
								if (b.getType() == Material.AIR) {
									b.setType(Material.FIRE);
									if (b.getType() == Material.FIRE) {
										blockCombustors.put(b.getLocation(), p.getName());
										index += 1;
										final int modifier = index;
										Bukkit.getScheduler().runTaskLater(KitPVP.get(), new Runnable() {

											@Override
											public void run() {
												blockCombustors.remove(b.getLocation());
												b.setType(Material.AIR);
											}
										}, (modifier) * 1);
									}
								}

							}
							KitAPI.getStatManager().getPlayerData(e.getPlayer().getName()).getPlayerKitData().get(Inferno.this).incrementAbility(index - 1);
							e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.BLAZE_BREATH, 1F, 20F);
							e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1F, 0.1F);
						}
					}
				}
			}
		};
	}

	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		inv.setHelmet(Utilities.generateLeatherArmor(Material.LEATHER_HELMET, Color.RED, 1));
		inv.setChestplate(Utilities.generateItem(Material.IRON_CHESTPLATE));
		inv.setLeggings(Utilities.generateItem(Material.IRON_LEGGINGS));
		inv.setBoots(Utilities.generateLeatherArmor(Material.LEATHER_BOOTS, Color.RED, 1));
		inv.setItem(0, Utilities.generateItem(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 1));
		inv.setItem(1, Core.get().generateItem(Material.BLAZE_POWDER, 0, "§cFlame Dust", new String[] { "§9Right click to ignite the", "§9blocks around you!" }));
		return inv;
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] { new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2147483647, 0) };
	}

	@Override
	public String getDescription() {
		return "Engulf the blocks around you in fire!";
	}

	@Override
	public int getId() {
		return 4;
	}

	@Override
	public Material getIconMaterial() {
		return Material.BLAZE_POWDER;
	}

	@Override
	public String getMetaName() {
		return "Blocks ignited";
	}
}
