package net.frozenorb.KitPVP.Commands;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Kill extends BaseCommand {
	public String[] aliases = new String[] { "suicide", "slay" };

	@Override
	public void syncExecute() {
		if (args.length > 0) {
			Player p = Bukkit.getPlayer(args[0]);
			if (p == null) {
				sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' not found.");
				return;
			}
			p.setHealth(0D);
			KitAPI.getServerManager().handleRespawn(p);
			sender.sendMessage("Killed " + ChatColor.GOLD + p.getName());
		} else {
			((Player) sender).setHealth(0D);
			sender.sendMessage("Killed " + ChatColor.GOLD + sender.getName());
			KitAPI.getServerManager().handleRespawn(((Player) sender));
		}
	}
}
