package net.frozenorb.KitPVP.Commands;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.ChatColor;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.mBasic.Basic;

public class Balance extends BaseCommand {
	public String[] aliases = new String[] { "bal", "$" };

	@Override
	public void execute() {
		if (args.length == 1) {
			String m = args[0];
			sender.sendMessage(ChatColor.GOLD + "Credits: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(m)));
			return;
		}
		sender.sendMessage(ChatColor.GOLD + "Credits: " + ChatColor.WHITE + NumberFormat.getNumberInstance(Locale.US).format(Basic.get().getEconomyManager().getBalance(sender.getName())));
	}
}
