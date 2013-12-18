package net.frozenorb.KitPVP.Commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Subcommand;
import net.frozenorb.KitPVP.StatSystem.LocalPlayerData;
import net.frozenorb.KitPVP.StatSystem.StatObjective;
import net.frozenorb.Utilities.Core;

import org.bukkit.ChatColor;

import com.mongodb.BasicDBObject;

public class Convert extends BaseCommand {

	public Convert() {
		registerSubcommand(new Subcommand("rating", new String[] { "elo", "ranking" }) {

			@Override
			protected void syncExecute() {
				sender.sendMessage(ChatColor.RED + "Loading all ratings...");

				BasicDBObject elos = new BasicDBObject();
				for (LocalPlayerData lpd : KitAPI.getStatManager().getAllLocalData()) {
					elos.put(lpd.getName(), lpd.get(StatObjective.ELO));
				}
				sender.sendMessage(ChatColor.RED + "Saving ratings...");

				try {
					File eloFile = new File("ratings.json");
					eloFile.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(eloFile));
					writer.write(Core.get().formatDBObject(elos));
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.RED + "Ratings saved!");

			}
		});
		registerSubcommandsToTabCompletions();
		setPermissionLevel("op", "§cYou are not allowed to do this.");
	}

	@Override
	public void syncExecute() {
		sender.sendMessage(ChatColor.RED + "/convert <rating>");
	}
}
