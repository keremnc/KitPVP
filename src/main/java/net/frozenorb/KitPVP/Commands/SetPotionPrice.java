package net.frozenorb.KitPVP.Commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.Utilities.CommandSystem.CommandVerifier;

public class SetPotionPrice extends BaseCommand {
	public String[] aliases = new String[] { "SetPotionEffectPrice" };

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
		for (PotionEffectType p : PotionEffectType.values()) {
			if (p != null)
				if (p.getName().toLowerCase().startsWith(action.toLowerCase())) {
					results.add(p.getName());
				}
		}
		return results;
	}

	@Override
	public void execute() {
		if (sender.isOp()) {
			if (args.length > 1 && CommandVerifier.verifyInt(args[1]) && CommandVerifier.verifyInt(args[2])) {
				Player p = (Player) sender;
				if (p.getItemInHand() != null) {
					KitAPI.getArmorDataManager().setPrice(new PotionEffect(PotionEffectType.getByName(args[0]), Integer.MAX_VALUE, Integer.parseInt(args[1])), Integer.parseInt(args[2]));
					sender.sendMessage(ChatColor.AQUA + "Price of " + Enchantment.getByName(args[0]).getName() + ":" + Integer.parseInt(args[1]) + " set to " + args[2] + ".");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "/setpotionprice <potion> <level> <price>");
			}
		}

	}
}
