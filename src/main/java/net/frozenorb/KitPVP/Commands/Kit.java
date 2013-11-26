package net.frozenorb.KitPVP.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Conversations.KitCreationConversation;
import net.frozenorb.KitPVP.DataSystem.Serialization.KitSerializer;
import net.frozenorb.KitPVP.KitSystem.SerializableKit;
import net.frozenorb.KitPVP.Utilities.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class Kit extends BaseCommand implements TabExecutor {
	public String[] aliases = new String[] { "selectkit" };
	public String description = "Kit command";

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] params) {
		LinkedList<String> args = new LinkedList<String>(Arrays.asList(params));
		LinkedList<String> results = new LinkedList<String>();
		String action = null;
		if (args.size() >= 1) {
			action = args.pop().toLowerCase();
		} else {
			return results;
		}
		ArrayList<String> kits = new ArrayList<String>();
		if (args.size() == 0) {
			for (net.frozenorb.KitPVP.KitSystem.Kit k : KitPVP.getKits()) {
				kits.add(k.getName());
			}
			if (sender.isOp())
				for (String str : new String[] { "_create", "_load", "_delete" }) {
					kits.add(str);
				}
		} else if (sender.isOp() && params.length > 0) {
			action = args.pop().toLowerCase();
			if (params[0].equalsIgnoreCase("_load")) {
				File dir = new File("data" + File.separator + "kits");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				for (File kitFile : dir.listFiles()) {
					if (kitFile.getName().toLowerCase().startsWith(action.toLowerCase()))
						results.add(kitFile.getName());
				}
			} else {
				for (net.frozenorb.KitPVP.KitSystem.Kit k : KitPVP.getKits()) {
					if (k.getName().toLowerCase().startsWith(action.toLowerCase()))
						results.add(k.getName());
				}

			}
		}

		for (String p : kits) {
			if (p.toLowerCase().startsWith(action.toLowerCase())) {
				results.add(p);
			}
		}
		return results;
	}

	@Override
	public void execute() {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("_create")) {
				new KitCreationConversation((Player) sender) {

					@Override
					public void onFinish(SerializableKit k) {
						if (KitAPI.getKitManager().getByName(k.getName()) != null) {
							sender.sendMessage(ChatColor.RED + "A kit by that name exists!");
							return;
						}
						sender.sendMessage("§eKit " + k.getName() + "§e has been created!");
						KitAPI.getKitManager().registerExternalKit(k);
					}
				};
				return;
			}
			if (args[0].equalsIgnoreCase("_delete")) {
				if (args.length > 1) {
					if (KitAPI.getKitManager().getByName(args[1]) != null) {
						net.frozenorb.KitPVP.KitSystem.Kit k = KitAPI.getKitManager().getByName(args[1]);
						if (k instanceof SerializableKit) {
							KitAPI.getKitManager().unregisterKit(k);
							sender.sendMessage("§eKit " + k.getName() + "§e has been deleted.");
							return;
						} else {
							sender.sendMessage("§cThat kit is not removable.");
							return;

						}
					}
					sender.sendMessage(ChatColor.RED + "Kit '" + args[1] + "' not found.");

				} else
					sender.sendMessage(ChatColor.RED + "/kit _delete <name>");
				return;
			}
			if (args[0].equalsIgnoreCase("_load")) {
				if (args.length > 1) {
					String name = args[1];
					new File("data" + File.separator + "kits").mkdir();
					File f = new File("data" + File.separator + "kits" + File.separator + name);
					if (f.exists()) {
						if (JSON.parse(Utilities.readFile(f)) != null) {
							BasicDBObject db = (BasicDBObject) JSON.parse(Utilities.readFile(f));
							SerializableKit kit = new KitSerializer().deserialize(db);
							if (KitAPI.getKitManager().getByName(kit.getName()) != null) {
								sender.sendMessage(ChatColor.RED + "A kit by that name exists!");
								return;
							}
							sender.sendMessage("§eKit " + kit.getName() + "§e has been loaded.");

							KitAPI.getKitManager().registerExternalKit(kit);
							return;
						}
					}
					File dir = new File("data" + File.separator + "kits");
					if (!dir.exists()) {
						dir.mkdirs();
					}
					for (File kitFile : dir.listFiles()) {
						if (kitFile.getName().split("\\.")[0].equalsIgnoreCase(name.split("\\.")[0])) {
							// read
							if (JSON.parse(Utilities.readFile(kitFile)) != null) {
								BasicDBObject db = (BasicDBObject) JSON.parse(Utilities.readFile(kitFile));
								SerializableKit kit = new KitSerializer().deserialize(db);
								if (KitAPI.getKitManager().getByName(kit.getName()) != null) {
									sender.sendMessage(ChatColor.RED + "A kit by that name exists!");
									return;
								}
								sender.sendMessage("§eKit " + kit.getName() + "§e has been loaded.");
								KitAPI.getKitManager().registerExternalKit(kit);
								return;
							}
						}
					}
					sender.sendMessage(ChatColor.RED + "File '" + name + "' could not be found, or could not be read.");
				} else
					sender.sendMessage(ChatColor.RED + "/kit _load <file>");
				return;
			}
			String kitName = args[0];
			if (KitAPI.getKitManager().getByName(kitName) != null) {
				net.frozenorb.KitPVP.KitSystem.Kit k = KitAPI.getKitManager().getByName(kitName);
				k.commandRun((Player) sender);
			} else {
				sender.sendMessage(String.format(ChatColor.RED + "Kit '%s' not found!", kitName));
			}

		} else {
			sender.sendMessage(ChatColor.RED + "/kit <kitName>");
		}
	}
}
