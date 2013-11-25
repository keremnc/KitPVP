package net.frozenorb.KitPVP.KitSystem.Kits;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.KitSystem.BaseKit;
import net.frozenorb.KitPVP.Utilities.Utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Archer extends BaseKit {

	@Override
	public Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onArrowFire(EntityShootBowEvent e) {
				if (e.getEntity() instanceof Player) {
					Player p = (Player) e.getEntity();
					if (KitAPI.getKitManager().getKitsOnPlayers().containsKey(p.getName()) && KitAPI.getKitManager().getKitsOnPlayers().get(p.getName()).getName().equals(getName()))
						KitAPI.getStatManager().getLocalData(p.getName()).getPlayerKitData().get(KitAPI.getKitManager().getByName(getName())).incrementAbility(1);
				}
			}
		};

	}

	@Override
	public PlayerInventory transformInventory(PlayerInventory inv) {
		inv.setArmorContents(new ItemStack[] { Utilities.generateItem(Material.IRON_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.PROTECTION_FALL, 4), new ItemStack(Material.LEATHER_LEGGINGS), Utilities.generateItem(Material.CHAINMAIL_CHESTPLATE, Enchantment.PROTECTION_PROJECTILE, 5), new ItemStack(Material.LEATHER_HELMET) });
		inv.setItem(0, Utilities.generateItem(Material.WOOD_SWORD, Enchantment.DAMAGE_ALL, 4, Enchantment.DURABILITY, 3));
		inv.setItem(1, Utilities.generateItem(Material.BOW, Enchantment.ARROW_DAMAGE, 4, Enchantment.ARROW_INFINITE, 1));
		inv.setItem(9, new ItemStack(Material.ARROW));
		return inv;
	}

	@Override
	public PotionEffect[] getPotionEffects() {
		return new PotionEffect[] { new PotionEffect(PotionEffectType.SPEED, 2147483647, 0) };
	}

	@Override
	public String getDescription() {
		return "Rule the map with your trusty bow and arrows.";
	}

	@Override
	public int getId() {
		return 2;
	}

	@Override
	public Material getIconMaterial() {
		return Material.BOW;
	}

	@Override
	public String getMetaName() {
		return "Arrows fired";
	}
}
