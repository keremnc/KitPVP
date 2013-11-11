package net.frozenorb.KitPVP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.ListenerSystem.Listeners.GeneralListener;

public class MuteChat extends BaseCommand {
	public String[] aliases = new String[] { "mc", "rc" };
	public String description = "Restrict chat";

	@Override
	public void execute() {
		if (sender.hasPermission("kitpvp.mutechat")) {
			GeneralListener.CHAT_MUTED = !GeneralListener.CHAT_MUTED;
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Chat has been " + (GeneralListener.CHAT_MUTED ? "muted." : "unmuted."));
		}
	}
}
