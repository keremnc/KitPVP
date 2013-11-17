package net.frozenorb.KitPVP.KitSystem;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.Events.PlayerKitSelectEvent;
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
	 * Gets the weight of the kit
	 * 
	 * @return weight
	 */
	@Override
	public int getWeight() {
		return 10;
	}

	/**
	 * Gets the kit name based on the class name
	 * 
	 * @return name of the kit
	 */
	@Override
	public String getName() {
		Kit k = this;
		return k.getClass().getSimpleName();
	}

	/**
	 * Gets the permission of the kit
	 * 
	 * @return permission
	 */
	@Override
	public String getPermission() {
		return "kitpvp.kit." + this.getName().toLowerCase().replace(" ", "");
	}

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

	/**
	 * Gets whether the player can use the kit
	 * 
	 * @param p
	 *            player
	 * @return true if can use
	 */
	public boolean hasKit(Player p) {
		return (Core.get().hasPermission(p, getPermission()));
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
		Kit k = this;
		if (KitAPI.getKitManager().hasKitOn(p.getName())) {
			sender.sendMessage(ChatColor.RED + "You may only use one kit per life!");
			return;
		}
		if (Core.get().hasPermission(p, k.getPermission())) {
			PlayerKitSelectEvent e = new PlayerKitSelectEvent(p, this);
			Bukkit.getPluginManager().callEvent(e);
			if (e.isCancelled())
				return;
			Core.get().clearPlayer(p);
			p.getInventory().setArmorContents(k.transformInventory(p.getInventory()).getArmorContents());
			p.getInventory().setContents(k.transformInventory(p.getInventory()).getContents());
			for (int i = 1; i < 36; i += 1)
				p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
			for (PotionEffect pot : k.getPotionEffects()) {
				p.addPotionEffect(pot);
			}
			KitAPI.getStatManager().getLocalData(p.getName()).getPlayerKitData().get(this).incrementUses(1);
			KitAPI.getKitManager().getKitsOnPlayers().put(p.getName(), k);
			p.sendMessage("§6You have chosen the kit §a" + k.getName() + "§6.");
		} else {
			p.sendMessage(ChatColor.RED + "You do not have access to that kit!");
		}
	}

	/**
	 * Runs the equip method when the kit name command is run
	 */
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
	public final ItemStack getKitIcon() {
		Attributes attr = new Attributes(getIcon());
		attr.clear();
		return attr.getStack();
	}
}
