package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.mBasic.Basic;

public class Repair extends BaseCommand {

	@Override
	public void syncExecute() {
		if (((Player) sender).getInventory() != null) {
			double x = Basic.get().getEconomyManager().getBalance(sender.getName());
			if (x >= 120) {
				sender.sendMessage(ChatColor.BLUE + "§6You have repaired your armor for §f120CR§6!");
				Basic.get().getEconomyManager().withdrawPlayer(sender.getName(), 120);
				for (ItemStack armor : ((Player) sender).getInventory().getArmorContents()) {
					armor.setDurability((short) 0);
				}
				for (ItemStack armor : ((Player) sender).getInventory()) {
					if (armor != null) {
						if (armor.getType() != (Material.POTION)) {
							if (armor.getType() != Material.GOLDEN_APPLE) {
								armor.setDurability((short) 0);
							}
						}
					}
				}

			} else {
				sender.sendMessage(ChatColor.RED + "This costs $120, while you only have " + x + ".");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Your inventory is empty!");
		}
	}

}
