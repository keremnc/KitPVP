package net.frozenorb.KitPVP.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

public class ReflectionManager {
	private SimpleCommandMap commandMap;

	public ReflectionManager() {
		try {
			commandMap = (SimpleCommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public SimpleCommandMap getCommandMap() {
		return commandMap;
	}

}
