package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Subcommand;

public class Debug extends BaseCommand {
	private static int MINIMUM_MS = 1;
	private static boolean VERBOSE = false;

	public Debug() {
		setPermissionLevel("kit.debug", "§cYou are not allowed to do this!");
		registerSubcommand(new Subcommand("filter") {

			@Override
			protected void syncExecute() {

				if (args.length > 1) {
					try {
						MINIMUM_MS = Integer.parseInt(args[1]);
						sender.sendMessage(ChatColor.GREEN + "Debug filter set to " + MINIMUM_MS + "ms.");
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "That isn't a number!");
					}
				}

			}
		});
		registerSubcommand(new Subcommand("toggle") {

			@Override
			protected void syncExecute() {
				VERBOSE = !VERBOSE;
				sender.sendMessage(ChatColor.YELLOW + "Verbose mode is now " + VERBOSE + ".");
			}
		});
		registerSubcommand(new Subcommand("1v1") {

			@Override
			protected void syncExecute() {

				sender.sendMessage(ChatColor.YELLOW + "1v1 match queue: " + KitAPI.getMatchManager().getMatches().toString());
				sender.sendMessage(" ");
				sender.sendMessage(ChatColor.YELLOW + "1v1 reqest dump: " + KitAPI.getMatchManager().getCurrentMatches().toString());

			}
		});
		registerSubcommandsToTabCompletions();
	}

	public static int getFiler() {
		return MINIMUM_MS;
	}

	public static void handleTiming(long s, String id) {
		long now = System.currentTimeMillis();
		if (now - s > MINIMUM_MS && VERBOSE) {
			System.out.println("MS FILTER FAIL: (" + id + ") - " + (now - s) + "ms");
		}
	}

	@Override
	public void syncExecute() {}
}
