package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Subcommand;
import net.frozenorb.KitPVP.StatSystem.Stat;
import net.frozenorb.KitPVP.StatSystem.StatObjective;

public class SetStat extends BaseCommand {

	public SetStat() {
		setPermissionLevel("kit.setstat", "§cYou do not have permission to do this.");
		for (final StatObjective so : StatObjective.values()) {
			if (so.isDisplay() && !so.isLocal()) {
				registerSubcommand(new Subcommand(so.getName()) {

					@Override
					protected void syncExecute() {
						if (args.length > 2) {
							String name = args[1];
							try {
								Stat s = KitAPI.getStatManager().getStat(name);
								if (s == null) {
									sender.sendMessage(ChatColor.RED + "That player is not online!");
									return;
								}
								s.set(so, Integer.parseInt(args[2]));
								sender.sendMessage(ChatColor.GREEN + so.getFriendlyName() + " for '" + args[1] + "' has been set to " + args[2] + ".");
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "/setstat <objective> <player> <value>");

						}
					}
				});
			}
		}
		registerSubcommandsToTabCompletions();
	}

	@Override
	public void syncExecute() {
		sender.sendMessage(ChatColor.RED + "/setstat <objective> <player> <value>");
	}
}
