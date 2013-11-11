package net.frozenorb.KitPVP.Commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class DelShortcutWarp extends BaseCommand {
	public String description = "Deletes a registered command";

	@Override
	public List<String> tabComplete() {
		LinkedList<String> params = new LinkedList<String>(Arrays.asList(args));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (params.size() >= 1) {
			action = params.pop().toLowerCase();
		} else {
			return results;
		}
		for (String p : SetShortcutWarp.getWarps()) {
			if (p.toLowerCase().startsWith(action.toLowerCase())) {
				results.add(p);
			}
		}
		return results;
	}

	@Override
	public void execute() {
		if (sender.hasPermission("kitpvp.setwarp")) {
			if (args.length > 0) {
				if (SetShortcutWarp.getWarps().contains(args[0])) {
					KitPVP.get().getCommandManager().unregisterCommand(args[0]);
					sender.sendMessage(ChatColor.RED + "Unregistered.");
					SetShortcutWarp.getWarps().remove(args[0]);
					KitAPI.getWarpDataManager().getData().remove(args[0]);
					KitAPI.getWarpDataManager().saveData();
				} else {
					sender.sendMessage(ChatColor.RED + "Warp not found.");
				}
			}
		} else
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
	}
}
