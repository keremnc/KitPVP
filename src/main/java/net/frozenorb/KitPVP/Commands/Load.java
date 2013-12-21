package net.frozenorb.KitPVP.Commands;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Subcommand;
import net.frozenorb.Utilities.Core;
import net.frozenorb.mBasic.Basic;
import net.minecraft.util.com.google.common.io.Files;

public class Load extends BaseCommand {

	public Load() {
		registerSubcommand(new Subcommand("balances", new String[] { "money", "bal" }) {

			@Override
			protected void syncExecute() {
				File f = new File("balances.json");
				if (!f.exists()) {
					sender.sendMessage("§cFile balances.json does not exist");
					return;
				}
				String data = Core.get().readFile(f);
				try {
					BasicDBObject db = (BasicDBObject) JSON.parse(data);
					for (Entry<String, Object> e : db.entrySet()) {

						Basic.get().getEconomyManager().setBalance(Files.getNameWithoutExtension(e.getKey()), (double) e.getValue());
					}
				} catch (JSONParseException ex) {
					sender.sendMessage("§cError, file does not contain valid JSON!");
				}
			}
		});
		registerSubcommandsToTabCompletions();
		setPermissionLevel("op", "§cYou are not allowed to do this.");
	}

	@Override
	public void syncExecute() {
		sender.sendMessage(ChatColor.RED + "/load <balances>");
	}
}
