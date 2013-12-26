package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

public class SetKS extends BaseCommand {

	@Override
	public void syncExecute() {
		if (sender.isOp()) {
			if (args.length > 1) {
				String player = args[0];
				if (Bukkit.getPlayer(player) != null) {
					try {
						double v = Double.parseDouble(args[1]);
						Stat s = KitAPI.getStatManager().getStat(player);
						s.set(StatObjective.KILLSTREAK, (int) v);
						if (s.get(StatObjective.KILLSTREAK) > s.get(StatObjective.HIGHEST_KILLSTREAK)) {
							s.set(StatObjective.HIGHEST_KILLSTREAK, s.get(StatObjective.KILLSTREAK));
						}

					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Not a number.");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Player not found.");
				}
			} else
				sender.sendMessage(ChatColor.RED + "/setks <player> <ks>");
		}
	}
}
