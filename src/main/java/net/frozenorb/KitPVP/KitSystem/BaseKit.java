package net.frozenorb.KitPVP.KitSystem;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
import net.frozenorb.KitPVP.RegionSysten.RegionMeta;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.util.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Abstract base kit object
 * 
 * @author Kerem
 * @since 10/7/2013
 * 
 */
public abstract class BaseKit extends BaseCommand implements Kit {

	/*
	 * ---------------CLASS METHODS---------------
	 */

	/**
	 * Called when the kit is equipped, used for custom kit designs
	 */
	public void onEquip() {

	}

	/**
	 * Gets the Icon material of the kit
	 * 
	 * @return material
	 */
	public Material getIconMaterial() {
		return Material.POTATO;
	}

	/**
	 * Gets the Icon of the Kit
	 * 
	 * @return icon
	 */
	public ItemStack getIcon() {
		return new ItemStack(getIconMaterial());
	}

	/*
	 * --------------------OVERRIDEN METHODS------------------
	 */

	@Override
	public boolean hasKit(Player p) {
		return (Core.get().hasPermission(p, getPermission()));
	}

	@Override
	public String getMetaName() {
		return null;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String getPermission() {
		return "kitpvp.kit." + getName().toLowerCase().replace(" ", "");
	}

	@Override
	public void applyKit(Player p) {
		Core.get().clearPlayer(p);
		p.getInventory().setArmorContents(transformInventory(p.getInventory()).getArmorContents());
		p.getInventory().setContents(transformInventory(p.getInventory()).getContents());
		for (int i = 1; i < 36; i += 1)
			p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		for (PotionEffect pot : getPotionEffects()) {
			p.addPotionEffect(pot);
		}
		KitAPI.getStatManager().getLocalData(p.getName()).getPlayerKitData().get(this).incrementUses(1);
		KitAPI.getKitManager().getKitsOnPlayers().put(p.getName(), this);
		p.getInventory().setHeldItemSlot(0);
	}

	/*
	 * ----------FINAL METHODS-----------
	 */
	/**
	 * Equips the kit on the player
	 * 
	 * @param p
	 *            the player to receive the kit
	 */
	public final void equip(Player p) {
		if (KitAPI.getKitManager().hasKitOn(p.getName()) && !KitAPI.getPlayerManager().getSpawnProtection().contains(p.getName())) {
			sender.sendMessage(ChatColor.RED + "You may only use one kit per life!");
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

	@Override
	public final void execute() {
		equip((Player) sender);
		onEquip();

	}

	@Override
	public final String toString() {
		return getName();
	}

	@Override
	public final boolean hasAbilityMeta() {
		return getMetaName() != null;
	}

	@Override
	public final ItemStack getKitIcon() {
		Attributes attr = new Attributes(getIcon());
		attr.clear();
		return attr.getStack();
	}
}
