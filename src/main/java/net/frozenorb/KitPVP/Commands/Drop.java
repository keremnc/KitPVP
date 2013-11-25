package net.frozenorb.KitPVP.Commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Drop extends BaseCommand {

	@Override
	public void execute() {
		Player pl = (Player) sender;
		if (pl.getItemInHand() != null) {
			pl.setItemInHand(new ItemStack(Material.AIR));
			pl.getItemInHand().setType(Material.AIR);
		}
	}

}
