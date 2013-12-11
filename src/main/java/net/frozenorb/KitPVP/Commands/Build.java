package net.frozenorb.KitPVP.Commands;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.PlayerSystem.GamerProfile;
import net.frozenorb.Utilities.Core;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Build extends BaseCommand {

	@Override
	public void syncExecute() {
		if (Core.get().hasPermission(((Player) sender), "kitpvp.build")) {
			GamerProfile profile = KitAPI.getPlayerManager().getProfile(sender.getName());
			if (profile.isObject("build")) {
				profile.getJSON().append("build", false);
			} else {
				profile.getJSON().append("build", true);
			}
			sender.sendMessage(ChatColor.YELLOW + "Your build mode is now " + profile.isObject("build") + ".");
		}
	}

}
