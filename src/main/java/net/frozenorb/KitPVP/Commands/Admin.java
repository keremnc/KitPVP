package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Admin extends BaseCommand {

	@Override
	public void execute() {

		if (!(sender instanceof Player)) {
			return;
		}

		Player p = (Player) sender;

		if (!p.hasPermission("kitpvp.admin")) {
			return;
		}

		if (p.getGameMode() != GameMode.SURVIVAL) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
			p.setFlying(false);
			p.setAllowFlight(false);
			for (PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
			p.sendMessage(ChatColor.GRAY + "You are no longer in admin mode.");
			KitAPI.getPlayerManager().getProfile(p.getName()).getJSON().put("adminMode", false);
			final byte b = Byte.parseByte("0000000", 2);
			Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

				@Override
				public void run() {
					((CraftPlayer) sender).getHandle().getDataWatcher().watch(0, b);

				}
			}, 1L);
			return;
		}

		if (!KitAPI.getPlayerManager().getProfile(p.getName()).isObject("adminMode")) {
			p.setAllowFlight(true);
			p.setFlying(true);
			p.getActivePotionEffects().clear();
			p.setHealth(20D);
			p.setMaxHealth(20D);
			p.setHealth(20D);
			p.setFoodLevel(20);
			p.setLevel(0);
			p.setFireTicks(0);
			p.setExp(0.0F);
			PlayerInventory inv = p.getInventory();
			inv.clear();
			inv.setArmorContents(null);
			for (PotionEffect pot : p.getActivePotionEffects())
				p.removePotionEffect(pot.getType());
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
			p.sendMessage(ChatColor.GRAY + "You are now in admin mode.");

			KitAPI.getPlayerManager().getProfile(p.getName()).getJSON().put("adminMode", true);
			final byte b = Byte.parseByte("0110000", 2);
			Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

				@Override
				public void run() {
					((CraftPlayer) sender).getHandle().getDataWatcher().watch(0, b);

				}
			}, 1L);
		} else {
			p.setFlying(false);
			p.setAllowFlight(false);
			for (PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
			p.sendMessage(ChatColor.GRAY + "You are no longer in admin mode.");
			KitAPI.getPlayerManager().getProfile(p.getName()).getJSON().put("adminMode", false);
			final byte b = Byte.parseByte("0000000", 2);
			Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

				@Override
				public void run() {
					((CraftPlayer) sender).getHandle().getDataWatcher().watch(0, b);

				}
			}, 1L);
		}

	}
}
