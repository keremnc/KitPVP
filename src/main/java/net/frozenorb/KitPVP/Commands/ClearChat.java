package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class ClearChat extends BaseCommand {
	public String[] aliases = new String[] { "cc" };
	public String description = "Clear chat";

	@Override
	public void execute() {
		if (sender.hasPermission("kitpvp.clearchat")) {
			for (int i = 0; i < 128; i += 1) {
				Bukkit.broadcastMessage("");
			}
			Bukkit.broadcastMessage("Chat has been cleared by " + sender.getName());
		}
	}
}
