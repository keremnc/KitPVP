package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Kit extends BaseCommand implements TabExecutor {
	public String[] aliases = new String[] { "selectkit" };
	public String description = "Kit command";

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
		for (net.frozenorb.KitPVP.KitSystem.Kit k : KitPVP.getKits()) {
			kits.add(k.getName());
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
		if (args.length > 0) {
			String kitName = args[0];
			if (KitAPI.getKitManager().getByName(kitName) != null) {
				net.frozenorb.KitPVP.KitSystem.Kit k = KitAPI.getKitManager().getByName(kitName);
				((Player) sender).chat("/" + k.getName());
			} else {
				sender.sendMessage(String.format(ChatColor.RED + "Kit '%s' not found!", kitName));
			}

		} else {
			sender.sendMessage(ChatColor.RED + "/kit <kitName>");
		}
	}
}
