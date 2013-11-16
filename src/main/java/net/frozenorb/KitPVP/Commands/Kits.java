package net.frozenorb.KitPVP.Commands;

import java.util.ArrayList;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.KitSystem.Kit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Kits extends BaseCommand {
	public String[] aliases = new String[] { "mykits" };
	public String description = "Kit command";

	@Override
	public void execute() {

		Player s = (Player) sender;
		String kitStr = ChatColor.WHITE + "Kits: " + ChatColor.GREEN;
		boolean firstKit = true;
		ArrayList<String> kitz = new ArrayList<String>();

		for (Kit ki : KitPVP.getKits()) {
			if (s.hasPermission(ki.getPermission())) {
				kitz.add("§a" + ki.getName());
			}
		}
		for (Kit ki : KitPVP.getKits()) {
			if (!(s.hasPermission(ki.getPermission()))) {
				kitz.add("§c" + ki.getName());
			}

		}

		for (String k : kitz) {

			if (!firstKit) {
				kitStr = kitStr + "§f, ";
			}
			kitStr = kitStr + k;
			firstKit = false;
		}
		s.sendMessage(kitStr);
		s.sendMessage("To use a kit, type " + ChatColor.GOLD + "/<kitname> " + ChatColor.WHITE + "or " + ChatColor.GOLD + "/kit <kitName>" + ChatColor.WHITE + ".");

	}
}
