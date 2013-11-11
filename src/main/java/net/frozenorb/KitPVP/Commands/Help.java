package net.frozenorb.KitPVP.Commands;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;

public class Help extends BaseCommand {

	@Override
	public void execute() {
		String s = "§7====================================================\n§6KitPVP §fHelp Page\n" + "§7====================================================\n" + "§6/kits §f- Displays the kits available to you.\n" + "§6/stats §f- This will show you statistics!\n" + "§6/who §f- List the players that are currently online.\n" + "§6/1v1 §f- Warp to the 1v1 arena!\n" + "§6/repair §f- Repair your armor (Disabled in custom kits!)\n" + "§6/soup §f- Refill your soups. You will be vulnerable for 5 seconds.\n" + "§7====================================================";
		sender.sendMessage(s);
	}

}
