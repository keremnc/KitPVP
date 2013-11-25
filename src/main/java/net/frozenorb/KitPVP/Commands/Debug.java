package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Debug extends BaseCommand {
	private ArrayList<String> subCommands = new ArrayList<String>() {
		private static final long serialVersionUID = 4663536795533900598L;

		{
			add("1v1");
		}
	};

	@Override
	public List<String> tabComplete() {
		LinkedList<String> args = new LinkedList<String>(Arrays.asList(super.args));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (args.size() >= 1) {
			action = args.pop().toLowerCase();
		} else {
			return results;
		}
		ArrayList<String> kits = new ArrayList<String>();
		for (String sub : subCommands) {
			kits.add(sub);
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
