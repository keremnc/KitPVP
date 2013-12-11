package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Debug extends BaseCommand {
	public Debug() {
		setTabCompletions(new String[] { "1v1" });
	}

	@Override
	public void syncExecute() {
		if (sender.hasPermission("debug")) {
			if (args.length > 0) {
				String subcmd = args[0];
				if (subcmd.equalsIgnoreCase("1v1")) {
					sender.sendMessage(ChatColor.YELLOW + "1v1 match queue: " + KitAPI.getMatchManager().getMatches().toString());
					sender.sendMessage(" ");
					sender.sendMessage(ChatColor.YELLOW + "1v1 reqest dump: " + KitAPI.getMatchManager().getCurrentMatches().toString());
				}
			}
		}
	}
}
