package net.frozenorb.KitPVP.KitSystem.Kits;

import java.util.HashMap;
import java.util.Map;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.BaseKit;
import net.frozenorb.KitPVP.Utilities.Utilities;
import net.frozenorb.Utilities.Core;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class Fisherman extends BaseKit {
	private Map<String, Integer> fishing = new HashMap<String, Integer>();

	@Override
	public Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onPlayerFishingEvent(PlayerFishEvent event) {

				if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
					if (!KitAPI.getPlayerManager().hasSpawnProtection((Player) event.getCaught())) {
						if (KitAPI.getKitManager().getKitOnPlayer(event.getPlayer().getName()).equals(Fisherman.this)) {
							Player p = event.getPlayer();
							if (((fishing.get(p.getName())).intValue() == 1)) {
								((Player) event.getCaught()).setNoDamageTicks(0);
								Location to = p.getLocation();
								to.setPitch(event.getCaught().getLocation().getPitch());
								to.setYaw(event.getCaught().getLocation().getYaw());
								((Player) event.getCaught()).sendMessage(ChatColor.GREEN + "You have been hooked by a fisherman!");
								event.getCaught().teleport(to, TeleportCause.END_PORTAL);
								KitAPI.getStatManager().getPlayerData(p.getName()).getPlayerKitData().get(Fisherman.this).incrementAbility(1);
								fishing.put(p.getName(), Integer.valueOf(0));
							}
						}
					} else {
						event.getPlayer().sendMessage(ChatColor.RED + "You can't use this here.");
					}
				}
			}

			@EventHandler
			public void onAttack(EntityDamageByEntityEvent event) {
				if ((event.getDamager() instanceof Fish)) {
					Fish hook = (Fish) event.getDamager();
					if (!(hook.getShooter() instanceof Player))
						return;
					Player player = (Player) hook.getShooter();
					if (KitAPI.getKitManager().getKitOnPlayer(player.getName()).equals(Fisherman.this)) {
						fishing.put(player.getName(), Integer.valueOf(1));
						((Player) hook.getShooter()).getItemInHand().setDurability((short) -10000);
					}

				}
			}
		};

	}

	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		ItemStack flower = Core.get().generateItem(Material.FISHING_ROD, 0, "§cFishing Rod", new String[] { ChatColor.BLUE + "" + "Attach your rod to a player to pull them in!" });
		inv.setHelmet(Utilities.generateLeatherArmor(Material.LEATHER_HELMET, Color.BLACK));
		inv.setChestplate(Utilities.generateItem(Material.IRON_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 3));
		inv.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		inv.setLeggings(new ItemStack(Material.IRON_BOOTS));
		sword.setDurability((short) -1000);
		flower.setDurability((short) -1000);
		inv.addItem(sword);
		inv.addItem(flower);
		return inv;
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] {};
	}

	@Override
	public String getDescription() {
		return "Reel in your opponents using your fishing rod.";
	}

	@Override
	public int getId() {
		return 5;
	}

	@Override
	public Material getIconMaterial() {
		return Material.FISHING_ROD;
	}

	@Override
	public String getMetaName() {
		return "Players hooked";
	}
}
