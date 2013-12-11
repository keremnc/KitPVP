package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Conversations.ArenaConversation;
import net.frozenorb.Utilities.CommandSystem.CommandVerifier;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arena extends BaseCommand {

	public Arena() {
		setTabCompletions(new String[] { "create", "override", "here", "warp" });
	}

	@Override
	public void syncExecute() {
		if (sender.hasPermission("kitpvp.arena.modify")) {
			if (args.length > 0) {
				Location l = ((Player) sender).getLocation();
				if (args[0].equalsIgnoreCase("warp")) {
					if (args.length > 1) {
						if (CommandVerifier.verifyInt(args[1])) {
							int id = Integer.parseInt(args[1]);
							net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena a = KitAPI.getArenaManager().getById(id);
							if (a == null) {
								sender.sendMessage(ChatColor.RED + "Arena #" + args[1] + " not found.");
								return;
							}
							((Player) sender).teleport(a.getFirstLocation());
							sender.sendMessage(ChatColor.YELLOW + "Teleported to §a#" + args[1] + "§e's first location.");
						} else {
							sender.sendMessage(ChatColor.RED + "/arena warp <id>");
						}
					}
				}
				if (args[0].equalsIgnoreCase("override")) {
					if (args.length > 1) {
						if (CommandVerifier.verifyInt(args[1])) {
							int id = Integer.parseInt(args[1]);
							net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena a = KitAPI.getArenaManager().getById(id);
							if (a == null) {
								sender.sendMessage(ChatColor.RED + "Arena #" + args[1] + " not found.");
								return;
							}
							new ArenaConversation((Player) sender, id) {

								@Override
								public void onCreate(Location l1, Location l2) {
									KitAPI.getArenaManager().addArena(l1, l2);
								}
							};
						} else {
							sender.sendMessage(ChatColor.RED + "/arena override <id>");
						}
					}

				}
				if (args[0].equalsIgnoreCase("here")) {
					HashMap<net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena, Double> distances = new HashMap<net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena, Double>();
					ArrayList<Double> dList = new ArrayList<Double>();
					sender.sendMessage(ChatColor.RED + "Scanning for nearby arenas.");
					for (net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena a : KitAPI.getArenaManager().getArenas()) {
						double d1 = a.getFirstLocation().distance(l);
						double d2 = a.getSecondLocation().distance(l);
						distances.put(a, Math.min(d1, d2));
						dList.add(Math.min(d1, d2));

					}
					Collections.sort(dList, new Comparator<Double>() {
						@Override
						public int compare(Double o1, Double o2) {
							return o1.compareTo(o2);
						}
					});
					for (Entry<net.frozenorb.KitPVP.MatchSystem.ArenaSystem.Arena, Double> en : distances.entrySet()) {
						if (en.getValue().equals(dList.get(0))) {
							sender.sendMessage(ChatColor.YELLOW + "The arena you are closest to is §a#" + en.getKey().getId() + "§e Type §a/arena warp " + en.getKey().getId() + "§e to go there.");

						}
					}

				} else if (args[0].equalsIgnoreCase("create")) {
					new ArenaConversation((Player) sender) {

						@Override
						public void onCreate(Location l1, Location l2) {
							KitAPI.getArenaManager().addArena(l1, l2);
						}
					};
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/arena <subcmd>");
			}
		}
	}
}
