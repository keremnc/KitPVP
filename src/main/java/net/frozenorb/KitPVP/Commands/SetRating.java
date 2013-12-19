package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

public class SetRating extends BaseCommand {

	@Override
	public void syncExecute() {
		if (sender.isOp()) {
			if (args.length > 1) {
				String player = args[0];
				if (Bukkit.getPlayer(player) != null) {
					try {
						double v = Double.parseDouble(args[1]);
						KitAPI.getStatManager().getStat(player).set(StatObjective.ELO, (int) v);

					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Not a number.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Player not found.");
				}
			} else
				sender.sendMessage(ChatColor.RED + "/setrating <player> <rating>");
		}
	}
}
