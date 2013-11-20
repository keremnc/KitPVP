package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class ForceKit extends BaseCommand implements TabExecutor {
	public String[] aliases = new String[] { "kitforce" };
	public String description = "Force a kit on a player";

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] params) {
		LinkedList<String> args = new LinkedList<String>(Arrays.asList(params));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (args.size() >= 1) {
			action = args.pop().toLowerCase();
		} else {
			return results;
		}
		ArrayList<String> kits = new ArrayList<String>();
		if (args.size() == 0) {
			for (net.frozenorb.KitPVP.KitSystem.Kit k : KitPVP.getKits()) {
				kits.add(k.getName());
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				results.add(p.getName());
			}
		}
		for (String p : kits) {
			if (p.toLowerCase().startsWith(action.toLowerCase())) {
				results.add(p);
			}
		}
		return results;
	}

	@Override
	public void execute() {
		if (!sender.hasPermission("kitpvp.forcekit"))
			return;
		if (args.length > 1) {
			Player player = Bukkit.getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found.");
				return;
			}
			String kitName = args[0];
			if (KitAPI.getKitManager().getByName(kitName) != null) {
				net.frozenorb.KitPVP.KitSystem.Kit k = KitAPI.getKitManager().getByName(kitName);
				sender.sendMessage(ChatColor.GREEN + "You have given the §e" + k.getName() + "§a kit to §e" + player.getName() + "§a.");
				k.applyKit(player);
			} else {
				sender.sendMessage(String.format(ChatColor.RED + "Kit '%s' not found!", kitName));
			}

		} else {
			sender.sendMessage(ChatColor.RED + "/forcekit <kit> <player>");
		}
	}
}
