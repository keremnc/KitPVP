package net.frozenorb.KitPVP.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.KitSystem.CustomKit;
import net.frozenorb.Utilities.Serialization.Serializers.InventorySerializer;

public class SaveKit extends BaseCommand {

	@Override
	public void execute() {

		if (args.length == 1) {
			final String name = args[0];
			final Player p = (Player) sender;
			final CustomKit kit = new CustomKit(name, new InventorySerializer().deserialize(new InventorySerializer().serialize(p.getInventory())), p.getInventory().getArmorContents(), p.getActivePotionEffects().toArray(new PotionEffect[] {}));
			kit.setPrice(KitAPI.getArmorDataManager().getTotalPrice((Player) sender));
			KitAPI.getKitManager().addCustomKit(kit, p.getName());
			KitAPI.getKitManager().saveCustomKits(p.getName());
			sender.sendMessage(ChatColor.GOLD + "Kit §f'" + args[0] + "'§6 has been saved. §fPrice: §6" + KitAPI.getArmorDataManager().getTotalPrice((Player) sender));
			return;
		}
		sender.sendMessage(ChatColor.RED + "/savekit <kitName>");
	}
}
