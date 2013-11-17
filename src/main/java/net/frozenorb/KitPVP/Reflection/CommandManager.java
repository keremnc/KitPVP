package net.frozenorb.KitPVP.Reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.frozenorb.KitPVP.KitPVP;
import net.frozenorb.KitPVP.API.KitAPI;
import net.frozenorb.KitPVP.CommandSystem.BaseCommand;
import net.frozenorb.KitPVP.Commands.SetShortcutWarp;
import net.frozenorb.Utilities.Serialization.Serializers.LocationSerializer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.BasicDBObject;

public class CommandManager {
	private YamlConfiguration config;
	private File configFile;
	public static Location DUEL_LOCATION;
	public static Location EARLY_HG_LOCATION;

	public CommandManager() {

		loadCommands(KitPVP.get(), "net.frozenorb.KitPVP.Commands");
		loadCommands(KitPVP.get(), "net.frozenorb.KitPVP.KitSystem.Kits");
	}

	public ConfigurationSection getConfigSection(String commandName) {

		ConfigurationSection section = config.getConfigurationSection(commandName);
		if (section == null) {
			section = config.createSection(commandName);
		}
		return section;
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
			public void execute() {
				final Player p = (Player) sender;
				BasicDBObject json = KitAPI.getPlayerManager().getProfile(sender.getName().toLowerCase()).getJSON();
				if (KitAPI.getPlayerManager().canWarp(p)) {
					p.sendMessage(ChatColor.GOLD + "Warped to §f" + commandName + "§6!");
					teleport(p, toWarp);

				} else {
					p.sendMessage(ChatColor.GRAY + "Someone is nearby! Warping in 5 seconds!");
					int taskId = Bukkit.getScheduler().runTaskLater(KitAPI.getKitPVP(), new Runnable() {

						@Override
						public void run() {
							p.closeInventory();
							if (KitAPI.getPlayerManager().getProfile(sender.getName().toLowerCase()).getJSON().containsField("warpTask")) {
								p.sendMessage(ChatColor.GOLD + "Warped to §f" + commandName + "§6!");
								KitAPI.getPlayerManager().getProfile(sender.getName().toLowerCase()).getJSON().remove("warpTask");
								teleport(p, toWarp);
							}
						}
					}, 5 * 20).getTaskId();
					json.put("warpTask", taskId);
				}
			}
		};
	}

	public void teleport(Player p, Location loc) {
		if (KitAPI.getRegionChecker().getRegion(loc) != null) {
			KitAPI.getRegionChecker().getRegion(loc).getMeta().onWarp(p);
		}
		p.teleport(loc);
		p.closeInventory();
	}

	public void load(File file) {

		configFile = file;
		try {
			config = new YamlConfiguration();
			if (!configFile.exists()) {
				save();
			}
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean loadCommand(JavaPlugin owningPlugin, CommandExecutor exc, boolean save) {

		File newFile = new File(owningPlugin.getDataFolder(), "commands.yml");
		if (configFile == null || !configFile.equals(newFile)) {
			load(newFile);
		}
		boolean modified = false;
		modified = startRegisteringCommand(exc, exc.getClass().getSimpleName().replace("Command", ""));
		if (save && modified)
			save();
		return modified;
	}

	public void loadCommands(JavaPlugin plugin, String packageName) {

		boolean saveConfig = false;
		for (Class<?> commandClass : ClassGetter.getClassesForPackage(plugin, packageName)) {
			if (CommandExecutor.class.isAssignableFrom(commandClass)) {
				try {
					CommandExecutor commandListener = (CommandExecutor) commandClass.newInstance();
					final boolean modified = loadCommand(plugin, commandListener, false);
					if (modified)
						saveConfig = true;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print(String.format("Error while loading comand '%s', see: %s", commandClass.getSimpleName().replace("Command", ""), e.getMessage()));
				}
			}
		}
		if (saveConfig)
			save();
	}

	@SuppressWarnings({ "unchecked" })
	public boolean loadConfig(ConfigurationSection section, CommandExecutor exc, String commandName) {

		try {
			boolean modified = false;
			if (!section.contains("CommandName")) {
				modified = true;
				section.set("CommandName", commandName);
			}
			if (!section.contains("EnableCommand")) {
				modified = true;
				section.set("EnableCommand", true);
			}
			for (Field field : exc.getClass().getDeclaredFields()) {
				if ((field.getName().equals("aliases") || field.getName().equals("description")) && !Modifier.isTransient(field.getModifiers()) && Modifier.isPublic(field.getModifiers()))
					try {
						Object value = section.get(field.getName());
						if (value == null) {
							value = field.get(exc);
							if (value instanceof String[]) {
								String[] strings = (String[]) value;
								String[] newStrings = new String[strings.length];
								for (int i = 0; i < strings.length; i++) {
									newStrings[i] = strings[i].replace("\n", "\\n").replace("§", "&").toLowerCase();
								}
								section.set(field.getName(), newStrings);
							} else {
								if (value instanceof String)
									value = ((String) value).replace("\n", "\\n").replace("§", "&");
								section.set(field.getName(), value);
							}
							modified = true;
						} else if (field.getType().isArray() && value.getClass() == ArrayList.class) {
							List<Object> array = (List<Object>) value;
							value = array.toArray(new String[array.size()]);
						}
						if (value instanceof String) {
							value = ChatColor.translateAlternateColorCodes('&', (String) value).replace("\\n", "\n");
						}
						if (value instanceof String[]) {
							String[] strings = (String[]) value;
							for (int i = 0; i < strings.length; i++)
								strings[i] = ChatColor.translateAlternateColorCodes('&', strings[i]).replace("\\n", "\n");
							value = strings;
						}
						if (field.getType().getSimpleName().equals("float") && value.getClass() == Double.class) {
							field.set(exc, ((float) (double) (Double) value));
						} else
							field.set(exc, value);
					} catch (Exception e) {
					}
			}
			return modified;
		} catch (Exception e) {
		}
		return false;
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
			if (exc.getClass().getSimpleName().equals("Creator")) {
				List<String> list = new ArrayList<String>();
				command.setAliases(list);
			}
		}
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

	public void save() {
		try {
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean startRegisteringCommand(CommandExecutor exc, String commandName) {
		ConfigurationSection section = getConfigSection(commandName);
		boolean modified = loadConfig(section, exc, commandName);
		if (section.getBoolean("EnableCommand") || exc.getClass().getSimpleName().equals("Creator")) {
			try {
				registerCommand(section.getString("CommandName"), exc);
			} catch (Exception ex) {
			}
		}
		return modified;
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
