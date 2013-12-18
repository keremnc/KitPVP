package net.frozenorb.KitPVP.CommandSystem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.Commands.SetShortcutWarp;
import net.frozenorb.Utilities.Serialization.Serializers.LocationSerializer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mongodb.BasicDBObject;

public class CommandManager {
	public static Location DUEL_LOCATION;
	public static Location EARLY_HG_LOCATION;

	public CommandManager() {

		loadCommands("net.frozenorb.KitPVP.Commands");
		loadCommands("net.frozenorb.KitPVP.KitSystem.Kits");
	}

	public void loadCommandsFromJson(BasicDBObject db) {
		for (String str : db.keySet()) {
			unregisterCommand(str);
			BasicDBObject warpLocation = (BasicDBObject) db.get(str);
			Location toLoc = new LocationSerializer().deserialize(warpLocation);
			CommandExecutor exc = generateCommandExecutor(str, toLoc);
			try {
				registerCommand(str, exc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generates a CommandExecutor for quick warp registration
	 * 
	 * @param commandName
	 *            the warpname
	 * @param toWarp
	 *            the location of the warp
	 * @return the generated command executor
	 */
	public CommandExecutor generateCommandExecutor(final String commandName, final Location toWarp) {
		SetShortcutWarp.getWarps().add(commandName);
		if (commandName.equalsIgnoreCase("1v1"))
			DUEL_LOCATION = toWarp;
		if (commandName.equalsIgnoreCase("hg"))
			EARLY_HG_LOCATION = toWarp;
		return new BaseCommand() {

			@Override
			public void syncExecute() {
				final Player p = (Player) sender;
				BasicDBObject json = KitAPI.getPlayerManager().getProfile(p.getName().toLowerCase()).getJSON();
				if (KitAPI.getPlayerManager().canWarp(p)) {
					p.sendMessage(ChatColor.GOLD + "Warped to §f" + commandName + "§6!");
					KitAPI.getPlayerManager().teleport(p, toWarp);

				} else {
					p.sendMessage(ChatColor.GRAY + "Someone is nearby! Warping in 5 seconds!");
					int taskId = Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

						@Override
						public void run() {
							p.closeInventory();
							if (KitAPI.getPlayerManager().getProfile(p.getName().toLowerCase()).getJSON().containsField("warpTask")) {
								p.sendMessage(ChatColor.GOLD + "Warped to §f" + commandName + "§6!");
								KitAPI.getPlayerManager().getProfile(p.getName().toLowerCase()).getJSON().remove("warpTask");
								KitAPI.getPlayerManager().teleport(p, toWarp);
							}
						}
					}, 5 * 20).getTaskId();
					json.put("warpTask", taskId);
				}
			}
		};
	}

	public void loadCommands(String packageName) {
		for (Class<?> commandClass : KitPVP.get().getReflectionManager().getClassesInPackage(packageName)) {
			if (CommandExecutor.class.isAssignableFrom(commandClass)) {
				try {
					CommandExecutor commandListener = (CommandExecutor) commandClass.newInstance();
					registerCommand(commandListener.getClass().getSimpleName().replace("Command", ""), commandListener);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print(String.format("Error while loading comand '%s', see: %s", commandClass.getSimpleName().replace("Command", ""), e.getMessage()));
				}
			}
		}
	}

	public void registerCommand(String name, CommandExecutor exc) throws Exception {
		PluginCommand command = Bukkit.getServer().getPluginCommand(name.toLowerCase());
		if (command == null) {
			Constructor<?> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			constructor.setAccessible(true);
			command = (PluginCommand) constructor.newInstance(name, KitPVP.get());
		}
		command.setExecutor(exc);
		try {
			Field field = exc.getClass().getDeclaredField("aliases");
			if (field.get(exc) instanceof String[]) {
				List<String> list = Arrays.asList((String[]) field.get(exc));
				command.setAliases(list);
			}
		} catch (Exception ex) {
		}
		if (exc instanceof BaseCommand)
			command.getAliases().addAll(Arrays.asList(((BaseCommand) exc).getAliases()));
		if (command.getAliases() != null) {
			for (String alias : command.getAliases())
				unregisterCommand(alias);
		}
		try {
			Field field = exc.getClass().getDeclaredField("description");
			if (field != null && field.get(exc) instanceof String)
				command.setDescription(ChatColor.translateAlternateColorCodes('&', (String) field.get(exc)));
		} catch (Exception ex) {
		}
		KitPVP.get().getReflectionManager().getCommandMap().register(name, command);
	}

	@SuppressWarnings("unchecked")
	public void unregisterCommand(String name) {
		try {
			Field known = SimpleCommandMap.class.getDeclaredField("knownCommands");
			Field alias = SimpleCommandMap.class.getDeclaredField("aliases");
			known.setAccessible(true);
			alias.setAccessible(true);
			Map<String, Command> knownCommands = (Map<String, Command>) known.get(KitPVP.get().getReflectionManager().getCommandMap());
			Set<String> aliases = (Set<String>) alias.get(KitPVP.get().getReflectionManager().getCommandMap());
			knownCommands.remove(name.toLowerCase());
			aliases.remove(name.toLowerCase());
		} catch (Exception ex) {

		}
	}
}
