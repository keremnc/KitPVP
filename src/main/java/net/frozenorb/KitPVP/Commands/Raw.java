package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Raw extends BaseCommand {

	@Override
	public void execute() {
		if (sender.isOp()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < args.length; i += 1) {
				sb.append(args[i] + " ");
			}
			String final_msg = sb.toString();
			Bukkit.getServer().broadcastMessage(final_msg.replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
		}
	}
}
