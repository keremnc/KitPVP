package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.minecraft.server.v1_7_R1.MinecraftServer;

public class MOTD extends BaseCommand {

	@Override
	public void syncExecute() {
		if (sender.isOp() && args.length > 0) {
			MinecraftServer.getServer().setMotd(args[0].replace("&", "§"));
			sender.sendMessage("§dMOTD is now:§r " + args[0].replace("&", "§"));
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
		}
	}

}
