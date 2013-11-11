package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Sc extends BaseCommand {
	public String[] aliases = new String[] { "ac" };

	@Override
	public void execute() {
		if (sender.hasPermission("kit.sc")) {
			if (args.length > 0) {
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < args.length; i += 1)
					b.append(args[i] + " ");
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("kit.sc")) {
						p.sendMessage(ChatColor.AQUA + sender.getName() + ": " + ChatColor.AQUA + b.toString());
					}
				}
				return;
			}

		}

	}

}
