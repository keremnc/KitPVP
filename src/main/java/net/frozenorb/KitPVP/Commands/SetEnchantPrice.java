package net.frozenorb.KitPVP.Commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.Utilities.CommandSystem.CommandVerifier;

public class SetEnchantPrice extends BaseCommand {
	public String[] aliases = new String[] { "SetEnchantmentPrice" };

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
		for (Enchantment p : Enchantment.values()) {
			if (p.getName().toLowerCase().startsWith(action.toLowerCase())) {
				results.add(p.getName());
			}
		}
		return results;
	}

	@Override
	public void execute() {
		if (sender.isOp()) {
			if (args.length > 1 && CommandVerifier.verifyEnchantment(args[0]) && CommandVerifier.verifyInt(args[1]) && CommandVerifier.verifyInt(args[2])) {
				Player p = (Player) sender;
				if (p.getItemInHand() != null) {
					KitAPI.getArmorDataManager().setPrice(Enchantment.getByName(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					sender.sendMessage(ChatColor.AQUA + "Price of " + Enchantment.getByName(args[0]).getName() + ":" + Integer.parseInt(args[1]) + " set to " + args[2] + ".");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/setenchantprice <enchantment> <price> <int>");
			}
		}

	}
}
