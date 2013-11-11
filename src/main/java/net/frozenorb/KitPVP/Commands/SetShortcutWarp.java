package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.Utilities.Serialization.Serializers.LocationSerializer;
import net.frozenorb.mShared.Shared;
import net.minecraft.server.v1_6_R3.MinecraftServer;

public class SetShortcutWarp extends BaseCommand {
	public String description = "Generates and sets a BaseCommand instance at runtime to warp to the Location";
	private static ArrayList<String> warps = new ArrayList<String>();

	public static ArrayList<String> getWarps() {
		return warps;
	}

	@Override
	public void execute() {
		if (sender.hasPermission("kitpvp.setwarp")) {
			if (args.length > 0) {
				Command cmd = Bukkit.getPluginCommand(args[0]);
				if (!warps.contains(args[0]))
					if (cmd != null || MinecraftServer.getServer().getCommandHandler().a().containsKey(args[0])) {
						sender.sendMessage(ChatColor.RED + "That command is already registered.");
						return;
					}
				String warpName = args[0];
				Location l = ((Player) sender).getLocation();
				KitAPI.getWarpDataManager().getData().put(warpName, new LocationSerializer().serialize(l));
				sender.sendMessage(ChatColor.YELLOW + "Warp <" + warpName + "> has been set to  " + Shared.get().getUtilities().formatDBObject(new LocationSerializer().serialize(l)) + "");
				sender.sendMessage(ChatColor.YELLOW + "Registering command /" + warpName);
				KitAPI.getKitPVP().getCommandManager().loadCommandsFromJson(KitAPI.getWarpDataManager().getData());
				sender.sendMessage(ChatColor.YELLOW + "Registered.");
				KitAPI.getWarpDataManager().saveData();
			}
		} else
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this.");
	}
}
