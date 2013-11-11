package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.Utilities.CommandSystem.CommandVerifier;

public class SetPrice extends BaseCommand {

	@Override
	public void execute() {
		if (sender.isOp()) {
			if (args.length > 0 && CommandVerifier.verifyInt(args[0])) {
				Player p = (Player) sender;
				if (p.getItemInHand() != null) {
					KitAPI.getArmorDataManager().setPrice(p.getItemInHand(), Integer.parseInt(args[0]));
					sender.sendMessage(ChatColor.AQUA + "Price of " + KitAPI.getArmorDataManager().getName(p.getItemInHand()) + " set to " + args[0] + ".");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/setprice <int>");
			}
		}

	}
}
