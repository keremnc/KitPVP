package net.frozenorb.KitPVP.Commands;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Help extends BaseCommand {

	@Override
	public void execute() {
		String[] s = { "§7====================================================", "§6KitPVP §fHelp Page", "§7====================================================", "§6/kits §f- Displays the kits available to you.", "§6/stats §f- This will show you statistics!", "§6/who §f- List the players that are currently online.", "§6/1v1 §f- Warp to the 1v1 arena!", "§6/repair §f- Repair your armor (Disabled in custom kits!)", "§6/soup §f- Refill your soups. You will be vulnerable for 5 seconds.", "§7====================================================" };
		sender.sendMessage(s);
	}
}
