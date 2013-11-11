package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Heal extends BaseCommand {

	@Override
	public void execute() {
		if (sender.hasPermission("basic.heal")) {
			if (args.length > 0) {
				if (Bukkit.getPlayer(args[0]) != null) {
					Bukkit.getPlayer(args[0]).setHealth(20D);
					sender.sendMessage("§6" + Bukkit.getPlayer(args[0]).getName() + "§f has been healed.");
				} else {
					sender.sendMessage("§cPlayer not found.");
				}
			} else {
				((Player) sender).setHealth(20D);
				sender.sendMessage(ChatColor.GOLD + "You have been healed.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
		}
	}

}
