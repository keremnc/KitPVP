package net.frozenorb.KitPVP.Commands;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.CommandSystem.Subcommand;
import net.frozenorb.KitPVP.MatchSystem.Match;
import net.frozenorb.Utilities.Core;
import net.frozenorb.Utilities.Message.JSONChatClickEventType;
import net.frozenorb.Utilities.Message.JSONChatColor;
import net.frozenorb.Utilities.Message.JSONChatExtra;
import net.frozenorb.Utilities.Message.JSONChatFormat;
import net.frozenorb.Utilities.Message.JSONChatHoverEventType;
import net.frozenorb.Utilities.Message.JSONChatMessage;

public class Debug extends BaseCommand {
	private static int MINIMUM_MS = 1;
	private static boolean VERBOSE = false;

	public Debug() {
		setPermissionLevel("kit.debug", "§cYou are not allowed to do this!");
		registerSubcommand(new Subcommand("filter") {

			@Override
			protected void syncExecute() {

				if (args.length > 1) {
					try {
						MINIMUM_MS = Integer.parseInt(args[1]);
						sender.sendMessage(ChatColor.GREEN + "Debug filter set to " + MINIMUM_MS + "ms.");
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "That isn't a number!");
					}
				}

			}
		});
		registerSubcommand(new Subcommand("toggle") {

			@Override
			protected void syncExecute() {
				VERBOSE = !VERBOSE;
				sender.sendMessage(ChatColor.YELLOW + "Verbose mode is now " + VERBOSE + ".");
			}
		});
		registerSubcommand(new Subcommand("1v1") {

			@Override
			protected void syncExecute() {

				sender.sendMessage(ChatColor.YELLOW + "1v1 match queue: " + KitAPI.getMatchManager().getMatches().toString());
				sender.sendMessage(" ");
				sender.sendMessage(ChatColor.YELLOW + "1v1 reqest dump: " + KitAPI.getMatchManager().getCurrentMatches().toString());

			}
		});
		registerSubcommand(new Subcommand("match") {

			@Override
			protected void syncExecute() {
				if (args.length > 1) {
					final String name = args[1];
					if (KitAPI.getMatchManager().isInMatch(name)) {
						Match m = KitAPI.getMatchManager().getCurrentMatches().get(name);
						if (m.isInProgress()) {
							JSONChatMessage jcm = new JSONChatMessage("", JSONChatColor.YELLOW, new ArrayList<JSONChatFormat>());
							jcm.addExtra(new JSONChatExtra("This match is in progress, click here to debug.", JSONChatColor.YELLOW, new ArrayList<JSONChatFormat>()) {
								{
									setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, "§aClick to debug");
									setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/debug match " + name + " output=true");
								}
							});
							jcm.sendToPlayer((Player) sender);
						} else {
							JSONChatMessage jcm = new JSONChatMessage("", JSONChatColor.YELLOW, new ArrayList<JSONChatFormat>());
							jcm.addExtra(new JSONChatExtra("This match is NOT in progress, click here to debug.", JSONChatColor.RED, new ArrayList<JSONChatFormat>()) {
								{
									setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, "§aClick to debug");
									setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/debug match " + name + " output=true");
								}
							});
							jcm.sendToPlayer((Player) sender);

						}
					} else {
						sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not in a match.");
					}
				}
				if (args.length > 2) {
					if (args[2].equals("output=true")) {

						final String name = args[1];
						if (KitAPI.getMatchManager().isInMatch(name)) {
							Match m = KitAPI.getMatchManager().getCurrentMatches().get(name);
							Core.get().sendFormattedDBOBject(sender, objectToJson(m), "§e");
						} else {
							sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not in a match.");
						}

					}
				}
			}
		});
		registerSubcommandsToTabCompletions();
	}

	public static int getFiler() {
		return MINIMUM_MS;
	}

	public static void handleTiming(long s, String id) {
		long now = System.currentTimeMillis();
		if (now - s > MINIMUM_MS && VERBOSE) {
			System.out.println("MS FILTER FAIL: (" + id + ") - " + (now - s) + "ms");
		}
	}

	private static BasicDBObject getJSONRepresentation(Field f, Object o) throws IllegalArgumentException, IllegalAccessException {
		BasicDBObject data = new BasicDBObject();
		Object ob = f.get(o);
		data.append(f.getName(), ob == null ? "null" : ob.toString().replace('=', '-'));
		return data;
	}

	public static BasicDBObject objectToJson(Object object) {
		try {
			Class<?> c = object.getClass();
			BasicDBObject data = new BasicDBObject("className", c.getName());
			data.append("package", c.getPackage().getName());
			data.append("superClass", c.getSuperclass().getName());
			BasicDBList fields = new BasicDBList();
			for (Field f : c.getDeclaredFields()) {
				f.setAccessible(true);
				fields.add(getJSONRepresentation(f, object));
			}
			data.append("fields", fields);
			return data;
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
			return new BasicDBObject("exception", ex.getStackTrace()[0].toString());
		}
	}

	@Override
	public void syncExecute() {}
}
